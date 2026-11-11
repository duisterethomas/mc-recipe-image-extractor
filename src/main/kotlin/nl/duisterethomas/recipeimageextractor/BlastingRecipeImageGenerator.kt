package nl.duisterethomas.recipeimageextractor

import net.minecraft.network.chat.Component
import java.awt.image.BufferedImage
import java.lang.Integer.max


class BlastingRecipeImageGenerator(
    private val recipeInfo: BlastingRecipeInfo,
    private val icons: Map<String, BufferedImage>,
    private val labels: Map<Component, BufferedImage>?,
    private val labelWidths: Map<Component, Int>?
) : RecipeImageGenerator {
    override val width = max(178 + RecipeImageGenerator.recipeRight.width, if (labelWidths != null) recipeInfo.itemStacks.maxOf { labelWidths[it.item.description]!! } + 46 + 8 else 0)
    override val height = if (labels != null) 196 else 124

    override fun generate(imageWidth: Int, imageHeight: Int): BufferedImage {
        return BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB).apply {
            raster.setRect(0, 0, blastingBase.getData(0, 0, width, height))
            raster.setRect(0, height - 2, RecipeImageGenerator.recipeBottom.getData(0, 0, width, 2))
            raster.setRect(width - 2, 0, RecipeImageGenerator.recipeRight.getData(0, 0, 2, height))

            createGraphics().use {
                drawImage(icons[recipeInfo.slot.uniqueKey]!!, 10, 10, null)
                drawImage(icons[recipeInfo.output.uniqueKey]!!, 130, 46, null)

                if (labels != null) {
                    var y = 122
                    for (itemStack in recipeInfo.itemStacks.distinctBy { it.uniqueKey }) {
                        drawImage(icons[itemStack.copyWithCount(1).uniqueKey]!!, 8, y, null)
                        drawImage(labels[itemStack.item.description]!!, 46, y + 8, null)
                        y += 34
                    }
                }
            }
        }
    }

    companion object {
        val blastingBase: BufferedImage by lazy { RecipeImageGenerator.loadTexture("blasting_base.png") }
    }
}
