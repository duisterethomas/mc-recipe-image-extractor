package nl.duisterethomas.recipeimageextractor

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.world.item.ItemStack
import java.awt.Color
import java.awt.image.BufferedImage
import java.nio.file.Path
import javax.imageio.ImageIO
import kotlin.math.ceil
import kotlin.math.sqrt

class ItemIconsExtractor(
    itemStacks: List<ItemStack>,
    private val exportDir: Path,
) {
    private val chunks = itemStacks.chunked(1000).mapIndexed { index, itemStacks -> Chunk(itemStacks, index) }
    lateinit var icons: Map<String, BufferedImage>

    fun exportIcons(client: Minecraft) {
        client.sendMessage("Generating icons")
        var iconsCount = 0
        for(chunk in chunks) {
            chunk.exportIcons(Color(139, 139, 139))
            chunk.findMaskColor()
            chunk.exportIcons(chunk.maskColor)
            iconsCount += chunk.size
            client.sendMessage("Generated $iconsCount icons")
        }
    }

    fun importIcons() {
        for(chunk in chunks) chunk.eraseMaskColor()
        icons = HashMap<String, BufferedImage>().also {
            for(chunk in chunks) it += chunk.importIcons()
        }
    }

    private inner class Chunk(private val itemStacks: List<ItemStack>, index: Int) {
        private val iconsPath = exportDir.resolve("icons_$index.png")
        private val iconsStride = ceil(sqrt(itemStacks.size.toDouble())).toInt()
        val size = itemStacks.size
        lateinit var maskColor: Color

        fun exportIcons(background: Color) {
            val width = iconsStride * 34
            val height = iconsStride * 34
            val scaledWidth = iconsStride * 17
            val r = background.red / 255.0f
            val g = background.green / 255.0f
            val b = background.blue / 255.0f

            val client = Minecraft.getInstance()

            val nativeImage = renderToTexture(width, height, r, g, b, 2.0) {
                val context = GuiGraphics(client, client.renderBuffers().bufferSource())

                var x = 0
                var y = 0
                for(itemStack in itemStacks) {
                    context.renderItem(itemStack, x, y)
                    context.renderItemDecorations(client.font, itemStack, x, y)

                    x += 17
                    if(x >= scaledWidth) {
                        x = 0
                        y += 17
                    }
                }

                context.flush()
            }

            nativeImage.use { it.writeToFile(iconsPath.toFile()) }
        }

        private fun getImage(): BufferedImage {
            return ImageIO.read(iconsPath.toFile()).asARGB()
        }

        fun findMaskColor() {
            maskColor = findMaskColor(getImage())
        }

        fun eraseMaskColor() {
            val maskedImage = getImage().apply { applyMaskColor(maskColor, this) }
            ImageIO.write(maskedImage, "PNG", iconsPath.toFile())
        }

        fun importIcons(): Map<String, BufferedImage> {
            val iconsImage = ImageIO.read(iconsPath.toFile()).asARGB()

            return itemStacks.mapIndexed { index, itemStack ->
                val x = (index % iconsStride) * 34
                val y = (index / iconsStride) * 34

                val image = BufferedImage(34, 34, BufferedImage.TYPE_INT_ARGB).apply {
                    raster.setRect(0, 0, iconsImage.getData(x, y, width, height))
                }

                itemStack.uniqueKey to image
            }.toMap()
        }
    }
}
