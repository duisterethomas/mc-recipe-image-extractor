package nl.duisterethomas.recipeimageextractor

import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import java.awt.image.BufferedImage
import java.lang.Integer.max


class StonecuttingRecipeImageGenerator(
    private val recipeInfo: StonecuttingRecipeInfo,
    private val icons: Map<String, BufferedImage>,
    private val labels: Map<Item, BufferedImage>?,
    private val itemNameWidths: Map<Item, Int>?
) : RecipeImageGenerator {
    override val width = max(168 + RecipeImageGenerator.recipeRight.width, if (itemNameWidths != null) recipeInfo.items.maxOf { itemNameWidths[it]!! } + 46 + 8 else 0)
    override val height = if (labels != null) 160 else 88

    override fun generate(imageWidth: Int, imageHeight: Int): BufferedImage {
        return BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB).apply {
            raster.setRect(0, 0, stonecuttingBase.getData(0, 0, width, height))
            raster.setRect(0, height - 2, RecipeImageGenerator.recipeBottom.getData(0, 0, width, 2))
            raster.setRect(width - 2, 0, RecipeImageGenerator.recipeRight.getData(0, 0, 2, height))

            createGraphics().use {
                drawImage(icons[recipeInfo.slot.uniqueKey]!!, 10, 38, null)
                drawImage(icons[recipeInfo.output.uniqueKey]!!, 53, 12, null)
                drawImage(icons[recipeInfo.output.uniqueKey]!!, 120, 38, null)

                if (labels != null) {
                    var y = 86
                    for (item in recipeInfo.items) {
                        drawImage(icons[ItemStack(item, 1).uniqueKey]!!, 8, y, null)
                        drawImage(labels[item]!!, 46, y + 8, null)
                        y += 34
                    }
                }
            }
        }
    }

    companion object {
        val stonecuttingBase: BufferedImage by lazy { RecipeImageGenerator.loadTexture("stonecutting_base.png") }
    }
}
