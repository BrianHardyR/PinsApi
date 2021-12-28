package com.pins.api.service

import com.pins.api.entities.content.Media
import com.pins.api.entities.content.MediaType
import com.pins.api.exceptions.FileUploadError
import com.pins.api.repository.MediaRepository
import com.pins.api.utils.safe
import magick.ImageInfo
import magick.MagickImage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.awt.Rectangle
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption


@Service
class FileService {

    @Value("\${file.upload-dir}") lateinit var uploadPath : String
    @Value("\${file.processed-dir}") lateinit var processedPath : String
    @Autowired lateinit var mediaRepository: MediaRepository

    val magickImage : MagickImage by lazy {  MagickImage() }

    private fun getUploadPath(type: MediaType):Path{
        val path = Paths.get("$uploadPath/${type.name}")
        val folderExists = Files.exists(path)
        if (!folderExists){
            safe {
                Files.createDirectory(path)
            }
        }
        return path
    }

    private fun getProcessedPath(type: MediaType): Path{
        val path = Paths.get("$processedPath/${type.name}")
        val folderExists = Files.exists(path)
        if (!folderExists){
            safe {
                Files.createDirectory(path)
            }
        }
        return path
    }

    fun save(file : MultipartFile, model : FileModel):Media?{
        val path = getUploadPath(model.type)
        val stream = file.inputStream

        val fileExtension = file.originalFilename?.substring(file.originalFilename?.lastIndexOf(".") ?: throw FileUploadError()) ?: throw FileUploadError()
        val filePath = (model.name ?: "${model.type.name}_${System.currentTimeMillis()}") + fileExtension

        val target = path.resolve( filePath )
        val pathString = target.toUri().toString()
        print("File path $pathString\n")
        val writtenBytes = safe {
            Files.copy(stream,target, StandardCopyOption.REPLACE_EXISTING)
        }
        print("Bytes written $writtenBytes")
        return if (writtenBytes != null){
            val media = Media( type = model.type, url = "${model.type}/$filePath")
            mediaRepository.save(media)
        }else {
            throw FileUploadError()
        }
    }


    /**
     * Check if image is processed to requested h and w
     * if not process the image and save it
     * then return processed image
     */
    fun get(type : MediaType,fileName : String, height : Int?, width : Int?) : Resource {
        if (height == null || width == null) // return default image
            return get(type, fileName)
        // crop image first before returning
        val path = getUploadPath(type).resolve(fileName)
        crop(type,fileName,height, width)
        return get(type,fileName,true)
    }

    fun get(type : MediaType,fileName : String, processed : Boolean = false) : Resource {
        val path = if (processed) getProcessedPath(type).resolve(fileName) else getUploadPath(type).resolve(fileName)
        val resource = UrlResource(path.toUri())
        return if (resource.exists()) resource
        else throw FileNotFoundException("File with name $fileName not found")
    }

    fun crop(type : MediaType,fileName : String, height : Int?, width : Int?):Path{
        val uploadedPath = getUploadPath(type).resolve(fileName)
        if (height == null || width == null ) return uploadedPath
        val croppedPath = getProcessedPath(type).resolve(fileName)

        val magickInfo = ImageInfo(uploadedPath.toUri().toString())
        val magickImage = MagickImage(magickInfo)

        val ( originalHeight, originalWidth ) = magickImage.dimension.let { it.height to it.width }
        if (originalHeight < height || originalWidth < width) return uploadedPath
        magickImage.cropImage( Rectangle(width,height) )

        return croppedPath
    }

}

data class FileModel(
    val name : String?,
    val type : MediaType
)