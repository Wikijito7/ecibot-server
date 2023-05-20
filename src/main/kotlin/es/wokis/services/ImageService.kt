package es.wokis.services

import es.wokis.data.constants.ServerConstants.EMPTY_TEXT
import es.wokis.plugins.config
import es.wokis.utils.normalizeUrl
import io.ktor.http.content.*
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.Paths
import javax.imageio.ImageIO
import javax.naming.NoPermissionException


object ImageService {
    private const val IMAGE_EXTENSION = "jpg"
    private val imageFolder = config.getString("imageFolder")
    private val baseUri = config.getString("baseUri")
    private const val AVATARS = "avatars"

    fun getAvatar(id: String): File {
        val imagePath = "$imageFolder/$AVATARS/$id/$id.$IMAGE_EXTENSION"
        return getImage(imagePath)
    }

    private fun getImage(path: String): File {
        val defaultIcon = File("$imageFolder/default.png")
        val imagePath = Paths.get(path).normalize()

        val file = File(imagePath.toUri())

        return if (file.exists()) {
            file

        } else {
            defaultIcon
        }

    }

    fun insertAvatar(id: String, image: PartData.FileItem): String {
        return insertImage(AVATARS, id, image)
    }

    private fun insertImage(root: String, id: String, image: PartData.FileItem): String {
        try {
            val imageName = "$id.$IMAGE_EXTENSION"
            val imagePath = File("$imageFolder/$root/$id", imageName).normalize()
            val imageInputStream = image.streamProvider.invoke()
            val originalImage = ImageIO.read(imageInputStream)
            val newBufferedImage = BufferedImage(
                originalImage.width,
                originalImage.height,
                BufferedImage.TYPE_INT_RGB
            ).apply {
                createGraphics()
                    .drawImage(
                        originalImage,
                        0,
                        0,
                        Color.WHITE,
                        null
                    )
            }

            if (!imagePath.exists()) {
                imagePath.mkdirs()
            }

            ImageIO.write(newBufferedImage, IMAGE_EXTENSION, imagePath)

            imageInputStream.close()

            return "$baseUri/user/$id/avatar".normalizeUrl()

        } catch (exc: NoPermissionException) {
            /* no-op */
        }

        return EMPTY_TEXT
    }

}