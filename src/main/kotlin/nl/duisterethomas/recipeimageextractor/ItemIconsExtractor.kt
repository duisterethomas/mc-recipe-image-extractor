package nl.duisterethomas.recipeimageextractor

import net.minecraft.client.Minecraft
import net.minecraft.world.item.ItemStack
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
    lateinit var icons: Map<ItemStackKey, BufferedImage>

    fun exportIcons(client: Minecraft) {
        client.sendMessage("Generating icons")
        var iconsCount = 0
        for(chunk in chunks) {
            chunk.exportIcons(client)
            iconsCount += chunk.size
            client.sendMessage("Generated $iconsCount icons")
        }
    }

    fun importIcons() {
        icons = HashMap<ItemStackKey, BufferedImage>().also {
            for(chunk in chunks) it += chunk.importIcons()
        }
    }

    private inner class Chunk(private val itemStacks: List<ItemStack>, index: Int) {
        private val iconsPath = exportDir.resolve("icons_$index.png")
        private val iconsStride = ceil(sqrt(itemStacks.size.toDouble())).toInt()
        val size = itemStacks.size

        fun exportIcons(client: Minecraft) {
            val width = iconsStride * 34
            val height = iconsStride * 34
            val scaledWidth = iconsStride * 17

            val nativeImage = renderToTexture(client, width, height, 2.0) { graphics ->
                var x = 0
                var y = 0
                for(itemStack in itemStacks) {
                    graphics.renderItem(itemStack, x, y)
                    graphics.renderItemDecorations(client.font, itemStack, x, y)

                    x += 17
                    if(x >= scaledWidth) {
                        x = 0
                        y += 17
                    }
                }
            }

            nativeImage.use { ImageIO.write(it.toStraightAlpha(), "PNG", iconsPath.toFile()) }
        }

        fun importIcons(): Map<ItemStackKey, BufferedImage> {
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
