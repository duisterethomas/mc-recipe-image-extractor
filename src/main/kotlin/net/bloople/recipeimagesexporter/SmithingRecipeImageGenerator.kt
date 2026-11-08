package net.bloople.recipeimagesexporter

import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import java.awt.image.BufferedImage
import java.lang.Integer.max


class SmithingRecipeImageGenerator(
    private val recipeInfo: SmithingRecipeInfo,
    private val icons: Map<String, BufferedImage>,
    private val labels: Map<Item, BufferedImage>?,
    private val itemNameWidths: Map<Item, Int>?
) : RecipeImageGenerator {
    override val width = max(230 + RecipeImageGenerator.recipeRight.width, if (itemNameWidths != null) recipeInfo.items.maxOf { itemNameWidths[it]!! } + 46 + 8 else 0)
    override val height = if (labels != null) 190 else 52

    override fun generate(imageWidth: Int, imageHeight: Int): BufferedImage {
        return BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB).apply {
            raster.setRect(0, 0, smithingBase.getData(0, 0, width, height))
            raster.setRect(0, height - 2, RecipeImageGenerator.recipeBottom.getData(0, 0, width, 2))
            raster.setRect(width - 2, 0, RecipeImageGenerator.recipeRight.getData(0, 0, 2, height))

            createGraphics().use {
                drawImage(icons[recipeInfo.slotTemplate.uniqueKey]!!, 10, 10, null)
                drawImage(icons[recipeInfo.slotBase.uniqueKey]!!, 46, 10, null)
                drawImage(icons[recipeInfo.slotAddition.uniqueKey]!!, 82, 10, null)
                drawImage(icons[recipeInfo.output.uniqueKey]!!, 190, 10, null)

                if (labels != null) {
                    var y = 50
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
        val smithingBase: BufferedImage by lazy { RecipeImageGenerator.loadTexture("smithing_base.png") }
    }
}

