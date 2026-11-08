package net.bloople.recipeimagesexporter

import net.minecraft.world.item.ItemStack
import java.awt.image.BufferedImage
import java.lang.Integer.max


class CampfireCookingRecipeImageGenerator(
    private val recipeInfo: CampfireCookingRecipeInfo,
    private val itemsData: ItemsData
) : RecipeImageGenerator {
    override val width = max(166 + RecipeImageGenerator.recipeRight.width, recipeInfo.items.maxOf { itemsData.itemNameWidths[it]!! } + 46 + 8)
    override val height = campfireCookingBase.height

    override fun generate(imageWidth: Int, imageHeight: Int): BufferedImage {
        return BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB).apply {
            raster.setRect(0, 0, campfireCookingBase.getData(0, 0, width, height))
            raster.setRect(width - 2, 0, RecipeImageGenerator.recipeRight.getData(0, 0, 2, height))

            createGraphics().use {
                drawImage(itemsData.iconImage(recipeInfo.slot), 10, 10, null)
                drawImage(itemsData.iconImage(recipeInfo.output), 126, 10, null)

                var y = 50
                for(item in recipeInfo.items) {
                    drawImage(itemsData.iconImage(ItemStack(item, 1)), 8, y, null)
                    drawImage(itemsData.labelImage(item), 46, y + 8, null)
                    y += 34
                }
            }
        }
    }

    companion object {
        val campfireCookingBase: BufferedImage by lazy { RecipeImageGenerator.loadTexture("campfire_cooking_base.png") }
    }
}
