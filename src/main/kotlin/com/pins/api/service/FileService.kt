package com.pins.api.service

import com.pins.api.entities.content.Media
import com.pins.api.entities.content.MediaType
import com.pins.api.exceptions.FileUploadError
import com.pins.api.repository.MediaRepository
import com.pins.api.utils.safe
import org.apache.commons.io.IOUtils
import org.jobrunr.jobs.annotations.Job
import org.jobrunr.jobs.context.JobContext
import org.jobrunr.scheduling.JobScheduler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileNotFoundException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import kotlin.math.floor


@Service
class FileService {

    @Value("\${file.upload-dir}")
    lateinit var uploadPath: String

    @Value("\${file.processed-dir}")
    lateinit var processedPath: String

    @Autowired
    lateinit var mediaRepository: MediaRepository

    @Autowired lateinit var jobScheduler: JobScheduler

    val resolutions = arrayOf(144, 240, 360, 480, 720, 1080)

    fun getUploadPath(type: MediaType): Path {
        val path = Paths.get("$uploadPath/${type.name}")
        val folderExists = Files.exists(path)
        if (!folderExists) {
            safe {
                Files.createDirectory(path)
            }
        }
        return path
    }

    fun getProcessedPath(type: MediaType): Path {
        val path = Paths.get("$processedPath/${type.name}")
        val folderExists: (Path) -> Boolean = { path_ ->
            Files.exists(path_)
        }
        if (!folderExists(path)) {
            safe {
                Files.createDirectory(path)
            }
            if (!folderExists(path)) throw FileNotFoundException("Could not create folder")
        }
        return path
    }


    fun getExtension(originalFileName: String): String = originalFileName.substring(originalFileName?.lastIndexOf("."))


    fun save(file: MultipartFile, model: FileModel): Media? {
        val path = getUploadPath(model.type)
        val stream = file.inputStream

        val fileExtension = getExtension(file.originalFilename ?: throw FileUploadError())
        val filePath = (model.name ?: "${model.type.name}_${System.currentTimeMillis()}") + fileExtension

        val target = path.resolve(filePath)
        val pathString = target.toUri().toString()
        print("File path $pathString\n")
        val writtenBytes = safe {
            Files.copy(stream, target, StandardCopyOption.REPLACE_EXISTING)
        }
        print("Bytes written $writtenBytes")
        return if (writtenBytes != null) {
            val media = Media(type = model.type, url = "${model.type}/$filePath")
            mediaRepository.save(media)
        } else {
            throw FileUploadError()
        }.also {
            if (model.type == MediaType.Video) createDifferentResolutionVideos(filePath)
        }
    }


    /**
     * Check if image is processed to requested h and w
     * if not process the image and save it
     * then return processed image
     */
    fun get(type: MediaType, fileName: String, height: Int?, width: Int?): Resource {
        if (height == null || width == null) // return default image
            return get(getUploadPath(type).resolve(fileName))
        // crop image first before returning
        return get(resize(type, fileName, height, width))
    }

    /**
     * Get resource from path
     */
    fun get(path: Path): Resource {
        val resource = UrlResource(path.toUri())
        return if (resource.exists()) resource
        else throw FileNotFoundException("File with name ${path.toUri()} not found")
    }

    fun resourceExists(path: Path) = UrlResource(path.toUri()).exists()

    fun resize(type: MediaType, fileName: String, height: Int?, width: Int?): Path {
        val uploadedPath = getUploadPath(type).resolve(fileName)
        if (height == null || width == null) return uploadedPath

        val extension = getExtension(fileName)
        val croppedFileName = fileName.replace(extension, "")
        val fullCroppedFileName = "${croppedFileName}_resize_${height}x${width}${extension}"

        val croppedPath = getProcessedPath(type).resolve(fullCroppedFileName)

        if (resourceExists(croppedPath)) return croppedPath

        Files.copy(uploadedPath, croppedPath, StandardCopyOption.REPLACE_EXISTING)

        val scaled = executeCommand(
            getProcessedPath(type),
            "convert",
            fullCroppedFileName,
            "-resize",
            "${height}x${width}",
            fullCroppedFileName
        )

        return croppedPath
    }

    fun scale(type: MediaType, fileName: String, scale: Int?): Path {
        val uploadedPath = getUploadPath(type).resolve(fileName)
        if (scale == null) return uploadedPath

        val extension = getExtension(fileName)
        val stripedFileName = fileName.replace(extension, "")
        val fullStripedFileName = "${stripedFileName}_scale_${scale}${extension}"

        val scaledPath = getProcessedPath(type).resolve(fullStripedFileName)

        if (resourceExists(scaledPath)) return scaledPath

        Files.copy(uploadedPath, scaledPath, StandardCopyOption.REPLACE_EXISTING)

        val scaled = executeCommand(
            getProcessedPath(type),
            "convert",
            fullStripedFileName,
            "-resize",
            "$scale%",
            fullStripedFileName
        )
        return scaledPath
    }

    fun getVideo(fileName:String, resolution : Int?):Resource{
        val resource = if (resolution == null){
            val originalPath = getUploadPath(MediaType.Video).resolve(fileName)
            print("Getting video ${originalPath.toUri()}")
            get(originalPath)
        }else{
            val extension = getExtension(fileName)
            val trimmedFileName = fileName.replace(extension,"")
            val scaledFileName = "${trimmedFileName}_${resolution}${extension}"

            val scaledPath = getProcessedPath(MediaType.Video).resolve(scaledFileName)
            print("Getting video ${scaledPath.toUri()}")
            get(scaledPath)
        }

        return resource
    }

    private fun executeCommand(path: Path? = null, vararg command: String, jobContext: JobContext? = null): Boolean {
        print("Executing command ${path?.toUri()}\n")
        jobContext?.logger()?.info("Executing command ${path?.toUri()}")
        val builder = ProcessBuilder().apply {
            command(*command)
            directory(File(path?.toUri() ?: Paths.get("./").toUri() ))
        }
        print("Command to execute ${builder.command().joinToString(separator = " ") { "$it" }}")
        jobContext?.logger()?.info("Command to execute ${builder.command().joinToString(separator = " ") { "$it" }}")
        builder.redirectOutput(ProcessBuilder.Redirect.INHERIT)
        val process = builder.start()
        val output = IOUtils.toString(process.inputStream, StandardCharsets.UTF_8)
        jobContext?.logger()?.info(output)
        val exitCode = process.waitFor()
        print("Command executed $exitCode")
        return exitCode == 0
    }

    private fun executeCommandAndReturnResult(path: Path?, vararg command: String): String {
        print("Executing command ${path?.toUri()}\n")
        val builder = ProcessBuilder().apply {
            command(*command)
            directory(File(path?.toUri() ?: Paths.get("./").toUri() ))
        }
        print("Command to execute ${builder.command().joinToString(separator = " ") { "$it" }}")
        val output = IOUtils.toString(builder.start().inputStream, StandardCharsets.UTF_8)
        print("Command executed $output\n${builder.command()}")
        return output.trim()
    }

    fun createDifferentResolutionVideos(fileName: String) {
        val videoPath = getUploadPath(MediaType.Video)
        val originalVideoPath = videoPath.resolve(fileName)

        val resolution = getVideoResolution(videoPath,fileName)
        val resolutionsToProcess = getResolutionsToConvertTo(resolution)

        print("createDifferentResolutionVideos ${resolutionsToProcess.joinToString(separator = ", ") { "$it" }}")

        resolutionsToProcess.forEach {
            // schedule background process for each resolution
            val job = jobScheduler.enqueue { rescaleVideo(fileName,it, JobContext.Null) }

        }

    }

    @Job(retries = 2)
    fun rescaleVideo(fileName: String, resolution : Pair<Int,Int>, jobContext: JobContext) : Boolean{
        val (width,height) = resolution
        print("\nrescaleVideo ${width}x${height}")
        jobContext.logger().info("rescaleVideo $fileName ${width}x${height}")
        val extension = getExtension(fileName)
        val fileName_ = fileName.replace(extension,"")
        val newFileName = "${fileName_}_${resolution.second}${extension}"
        val originalPath = getUploadPath(MediaType.Video).resolve(fileName)
        val processedPath = getProcessedPath(MediaType.Video).resolve(newFileName)
        if (resourceExists(processedPath)) return true
        // ffmpeg -i 25.mp4 -vf scale 1080:-1 25_1080.mp4
        val result = executeCommand(
            null,
            "ffmpeg",
            "-i",
            originalPath.toUri().path,
            "-vf",
            "scale=$width:$height",
            processedPath.toUri().path
        )
        jobContext.logger().info("Done $result")
        return result
    }

    fun getResolutionsToConvertTo(original : Pair<Int,Int>):Array<Pair<Int,Int>>{
        val (width,height) = original
        val index = resolutions.indexOf(original.second)
        if (index == 0) {
            return arrayOf(original)
        } else if (index == -1){
            val validResolutions = resolutions.filter { it < height }.map { validHeight ->
                var unroundedWidth = (width.toDouble() / height) * validHeight
                val newWidth = getNextEvenNumber(unroundedWidth)
                newWidth to validHeight
            }
            return validResolutions.toTypedArray()
        }else{
            val validResolutions = resolutions.slice(0 until index).map { validHeight ->
                var unroundedWidth = (width.toDouble() / height) * validHeight
                val newWidth = getNextEvenNumber(unroundedWidth)
                newWidth to validHeight
            }
            return validResolutions.toTypedArray()
        }
    }

    fun getVideoResolution(directory: Path, fileName: String): Pair<Int, Int> {
        // ffprobe -v error -select_streams v -show_entries stream=width,height -of csv=p=0:s=x 25.mp4
        val result = executeCommandAndReturnResult(
            directory,
            "ffprobe",
            "-v",
            "error",
            "-select_streams",
            "v",
            "-show_entries",
            "stream=width,height",
            "-of",
            "csv=p=0:s=x",
            fileName
        )
        return result.split("x", ignoreCase = true).let { rSplit ->
            print("getVideoResolution $rSplit")
            if (rSplit.size != 2) throw IllegalArgumentException()
            rSplit[0].toInt() to rSplit[1].toInt()
        }
    }

    fun getNextEvenNumber(number: Double): Int {
        val round = floor(number / 2) * 2
        return round.toInt()
    }

}

data class FileModel(
    val name: String?,
    val type: MediaType
)