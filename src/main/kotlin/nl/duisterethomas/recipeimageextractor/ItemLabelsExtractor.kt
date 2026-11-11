package nl.duisterethomas.recipeimageextractor

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import java.awt.image.BufferedImage
import java.nio.file.Path
import javax.imageio.ImageIO

class ItemLabelsExtractor(
    itemStacks: List<ItemStack>,
    private val exportDir: Path,
    private val font: Font
) {
    private val chunks = itemStacks.distinctBy { it.item.description }.chunked(100).mapIndexed { index, itemStacks -> Chunk(itemStacks, index) }

    lateinit var widths: Map<Component, Int>
    lateinit var labels: Map<Component, BufferedImage>

    fun exportLabels(client: Minecraft) {
        client.sendMessage("Generating labels")
        var labelsCount = 0
        for(chunk in chunks) {
            chunk.exportLabels()
            labelsCount += chunk.size
            client.sendMessage("Generated $labelsCount labels")
        }
    }

    fun importLabels() {
        widths = HashMap<Component, Int>().also {
            for(chunk in chunks) it += chunk.widths
        }

        labels = HashMap<Component, BufferedImage>().also {
            for(chunk in chunks) it += chunk.importLabels()
        }
    }

    private inner class Chunk(private val itemStacks: List<ItemStack>, index: Int) {
        private val labelsPath = exportDir.resolve("labels_$index.png")
        val size = itemStacks.size
        val widths by lazy { itemStacks.associate { it.item.description to font.width(it.item.description) * 2 } }

        fun exportLabels() {
            val width = widths.values.max()
            val height = itemStacks.size * 18

            val nativeImage = renderToTexture(width, height, 2.0) {
                var y = 0
                for(itemStack in itemStacks) {
                    renderText(font, itemStack.item.description, 0, y, 0x000000)
                    y += 9
                }
            }

            nativeImage.use { ImageIO.write(it.toStraightAlpha(), "PNG", labelsPath.toFile()) }
        }

        fun importLabels(): Map<Component, BufferedImage> {
            val labelsImage = ImageIO.read(labelsPath.toFile()).asARGB()

            return itemStacks.mapIndexed { index, itemStack ->
                val y = index * 18
                val labelWidth = widths[itemStack.item.description]!!

                val image = BufferedImage(labelWidth, 18, BufferedImage.TYPE_INT_ARGB).apply {
                    raster.setRect(0, 0, labelsImage.getData(0, y, width, height))
                }

                itemStack.item.description to image
            }.toMap()
        }
    }
}
