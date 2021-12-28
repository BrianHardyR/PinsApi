package com.pins.api.service

import com.pins.api.entities.content.Media
import com.pins.api.entities.content.MediaType
import com.pins.api.exceptions.FileUploadError
import com.pins.api.repository.MediaRepository
import com.pins.api.utils.safe
import magick.MagickImage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption


@Service
class FileService {

    @Value("\${file.upload-dir}")
    lateinit var uploadPath: String

    @Value("\${file.processed-dir}")
    lateinit var processedPath: String

    @Autowired
    lateinit var mediaRepository: MediaRepository

    val magickImage: MagickImage by lazy { MagickImage() }

    private fun getUploadPath(type: MediaType): Path {
        val path = Paths.get("$uploadPath/${type.name}")
        val folderExists = Files.exists(path)
        if (!folderExists) {
            safe {
                Files.createDirectory(path)
            }
        }
        return path
    }

    private fun getProcessedPath(type: MediaType): Path {
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

    private fun executeCommand(path: Path, vararg command: String): Boolean {
        print("Executing command ${path.toUri()}\n")
        val builder = ProcessBuilder().apply {
            command(*command)
            directory(File(path.toUri()))
        }
        builder.command()
        val process = builder.start()
        val exitCode = process.waitFor()
        print("Command executed $exitCode ${builder.command()}")
        return exitCode == 0
    }

}

data class FileModel(
    val name: String?,
    val type: MediaType
)