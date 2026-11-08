package net.bloople.recipeimagesexporter

import java.awt.image.BufferedImage
import javax.imageio.ImageIO

interface RecipeImageGenerator {
    val width: Int
    val height: Int
    fun generate(imageWidth: Int = width, imageHeight: Int = height): BufferedImage

    companion object {
        private const val TEXTURE_DIR = "/assets/recipeimagesexporter/textures/"

        fun loadTexture(name: String): BufferedImage {
            val path = TEXTURE_DIR + name
            val stream = RecipeImageGenerator::class.java.getResourceAsStream(path)
                ?: error("Missing texture resource: $path")
            return stream.use { ImageIO.read(it) }.asARGB()
        }

        val recipeRight: BufferedImage by lazy { loadTexture("recipe_right.png") }
        val recipeBottom: BufferedImage by lazy { loadTexture("recipe_bottom.png") }
    }
}