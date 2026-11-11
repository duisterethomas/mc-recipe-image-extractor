package nl.duisterethomas.recipeimageextractor

import net.minecraft.network.chat.Component
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import kotlin.math.max

abstract class RecipeImageGenerator<T : RecipeInfo>(
    protected val recipeInfo: T,
    protected val icons: Map<ItemStackKey, BufferedImage>,
    private val labels: Map<Component, BufferedImage>?,
    private val labelWidths: Map<Component, Int>?,
    private val baseImage: BufferedImage,
    baseWidth: Int,
    val height: Int,
    private val labelsStartY: Int
) {
    val width: Int = max(
        baseWidth + recipeRight.width,
        if (labelWidths != null) recipeInfo.itemStacks.maxOf { labelWidths[it.item.description]!! } + 46 + 8 else 0
    )

    protected abstract fun Graphics2D.drawSlots()

    fun generate(imageWidth: Int = width, imageHeight: Int = height): BufferedImage {
        return BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB).apply {
            raster.setRect(0, 0, baseImage.getData(0, 0, imageWidth, imageHeight))
            raster.setRect(0, imageHeight - 2, recipeBottom.getData(0, 0, imageWidth, 2))
            raster.setRect(imageWidth - 2, 0, recipeRight.getData(0, 0, 2, imageHeight))

            val graphics = createGraphics()
            try {
                graphics.drawSlots()

                if (labels != null) {
                    var y = labelsStartY
                    for (itemStack in recipeInfo.itemStacks.distinctBy { it.uniqueKey }) {
                        graphics.drawImage(icons[itemStack.copyWithCount(1).uniqueKey]!!, 8, y, null)
                        graphics.drawImage(labels[itemStack.item.description]!!, 46, y + 8, null)
                        y += 34
                    }
                }
            }
            finally {
                graphics.dispose()
            }
        }
    }

    companion object {
        private const val TEXTURE_DIR = "/assets/$MOD_ID/textures/"

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