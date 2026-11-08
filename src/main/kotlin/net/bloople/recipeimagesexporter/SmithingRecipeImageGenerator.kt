package net.bloople.recipeimagesexporter

import net.minecraft.world.item.ItemStack
import java.awt.image.BufferedImage
import java.lang.Integer.max


class SmithingRecipeImageGenerator(
    private val recipeInfo: SmithingRecipeInfo,
    private val itemsData: ItemsData
) : RecipeImageGenerator {
    override val width = max(230 + RecipeImageGenerator.recipeRight.width, recipeInfo.items.maxOf { itemsData.itemNameWidths[it]!! } + 46 + 8)
    override val height = smithingBase.height

    override fun generate(imageWidth: Int, imageHeight: Int): BufferedImage {
        return BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB).apply {
            raster.setRect(0, 0, smithingBase.getData(0, 0, width, height))
            raster.setRect(width - 2, 0, RecipeImageGenerator.recipeRight.getData(0, 0, 2, height))

            createGraphics().use {
                drawImage(itemsData.iconImage(recipeInfo.slotTemplate), 10, 10, null)
                drawImage(itemsData.iconImage(recipeInfo.slotBase), 46, 10, null)
                drawImage(itemsData.iconImage(recipeInfo.slotAddition), 82, 10, null)
                drawImage(itemsData.iconImage(recipeInfo.output), 190, 10, null)

                val x = 8
                var y = 50

                for(item in recipeInfo.items) {
                    drawImage(itemsData.iconImage(ItemStack(item, 1)), x, y, null)
                    drawImage(itemsData.labelImage(item), 46, y + 8, null)
                    y += 34
                }
            }
        }
    }

    companion object {
        val smithingBase: BufferedImage by lazy { RecipeImageGenerator.loadTexture("smithing_base.png") }
    }
}

