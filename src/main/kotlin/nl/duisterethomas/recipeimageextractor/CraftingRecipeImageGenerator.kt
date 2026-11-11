package nl.duisterethomas.recipeimageextractor

import net.minecraft.network.chat.Component
import java.awt.image.BufferedImage
import java.lang.Integer.max


class CraftingRecipeImageGenerator(
    private val recipeInfo: CraftingRecipeInfo,
    private val icons: Map<ItemStackKey, BufferedImage>,
    private val labels: Map<Component, BufferedImage>?,
    private val labelWidths: Map<Component, Int>?
) : RecipeImageGenerator {
    val distinctItemStacks = recipeInfo.itemStacks.distinctBy { it.uniqueKey }

    override val width = max(214 + RecipeImageGenerator.recipeRight.width, if (labelWidths != null) recipeInfo.itemStacks.maxOf { labelWidths[it.item.description]!! } + 46 + 8 else 0)
    override val height = 122 + RecipeImageGenerator.recipeBottom.height + (if (labels != null) 8 + (distinctItemStacks.size * 30) + (max(distinctItemStacks.size - 1, 0) * 4) else 0)

    override fun generate(imageWidth: Int, imageHeight: Int): BufferedImage {
        return BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB).apply {
            raster.setRect(0, 0, craftingBase.getData(0, 0, width, height))
            raster.setRect(0, height - 2, RecipeImageGenerator.recipeBottom.getData(0, 0, width, 2))
            raster.setRect(width - 2, 0, RecipeImageGenerator.recipeRight.getData(0, 0, 2, height))

            createGraphics().use {
                if(recipeInfo.slot1 != null) drawImage(icons[recipeInfo.slot1.uniqueKey]!!, 10, 10, null)
                if(recipeInfo.slot2 != null) drawImage(icons[recipeInfo.slot2.uniqueKey]!!, 46, 10, null)
                if(recipeInfo.slot3 != null) drawImage(icons[recipeInfo.slot3.uniqueKey]!!, 82, 10, null)
                if(recipeInfo.slot4 != null) drawImage(icons[recipeInfo.slot4.uniqueKey]!!, 10, 46, null)
                if(recipeInfo.slot5 != null) drawImage(icons[recipeInfo.slot5.uniqueKey]!!, 46, 46, null)
                if(recipeInfo.slot6 != null) drawImage(icons[recipeInfo.slot6.uniqueKey]!!, 82, 46, null)
                if(recipeInfo.slot7 != null) drawImage(icons[recipeInfo.slot7.uniqueKey]!!, 10, 82, null)
                if(recipeInfo.slot8 != null) drawImage(icons[recipeInfo.slot8.uniqueKey]!!, 46, 82, null)
                if(recipeInfo.slot9 != null) drawImage(icons[recipeInfo.slot9.uniqueKey]!!, 82, 82, null)
                drawImage(icons[recipeInfo.output.uniqueKey]!!, 166, 46, null)

                if (labels != null) {
                    var y = 122
                    for (itemStack in distinctItemStacks) {
                        drawImage(icons[itemStack.copyWithCount(1).uniqueKey]!!, 8, y, null)
                        drawImage(labels[itemStack.item.description]!!, 46, y + 8, null)
                        y += 34
                    }
                }
            }
        }
    }

    companion object {
        val craftingBase: BufferedImage by lazy { RecipeImageGenerator.loadTexture("crafting_base.png") }
    }
}
