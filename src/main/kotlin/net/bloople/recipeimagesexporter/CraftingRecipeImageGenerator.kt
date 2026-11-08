package net.bloople.recipeimagesexporter

import net.minecraft.world.item.ItemStack
import java.awt.image.BufferedImage
import java.lang.Integer.max


class CraftingRecipeImageGenerator(
    private val recipeInfo: CraftingRecipeInfo,
    private val itemsData: ItemsData
) : RecipeImageGenerator {
    override val width = max(214 + RecipeImageGenerator.recipeRight.width, recipeInfo.items.maxOf { itemsData.itemNameWidths[it]!! } + 46 + 8)
    override val height = 124 + RecipeImageGenerator.recipeBottom.height + 6 + (recipeInfo.items.size * 30) + (max(recipeInfo.items.size - 1, 0) * 4)

    override fun generate(imageWidth: Int, imageHeight: Int): BufferedImage {
        return BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB).apply {
            raster.setRect(0, 0, craftingBase.getData(0, 0, width, height))
            raster.setRect(0, height - 2, RecipeImageGenerator.recipeBottom.getData(0, 0, width, 2))
            raster.setRect(width - 2, 0, RecipeImageGenerator.recipeRight.getData(0, 0, 2, height))

            createGraphics().use {
                if(recipeInfo.slot1 != null) drawImage(itemsData.iconImage(recipeInfo.slot1), 10, 10, null)
                if(recipeInfo.slot2 != null) drawImage(itemsData.iconImage(recipeInfo.slot2), 46, 10, null)
                if(recipeInfo.slot3 != null) drawImage(itemsData.iconImage(recipeInfo.slot3), 82, 10, null)
                if(recipeInfo.slot4 != null) drawImage(itemsData.iconImage(recipeInfo.slot4), 10, 46, null)
                if(recipeInfo.slot5 != null) drawImage(itemsData.iconImage(recipeInfo.slot5), 46, 46, null)
                if(recipeInfo.slot6 != null) drawImage(itemsData.iconImage(recipeInfo.slot6), 82, 46, null)
                if(recipeInfo.slot7 != null) drawImage(itemsData.iconImage(recipeInfo.slot7), 10, 82, null)
                if(recipeInfo.slot8 != null) drawImage(itemsData.iconImage(recipeInfo.slot8), 46, 82, null)
                if(recipeInfo.slot9 != null) drawImage(itemsData.iconImage(recipeInfo.slot9), 82, 82, null)
                drawImage(itemsData.iconImage(recipeInfo.output), 166, 46, null)

                var y = 122
                for(item in recipeInfo.items) {
                    drawImage(itemsData.iconImage(ItemStack(item, 1)), 8, y, null)
                    drawImage(itemsData.labelImage(item), 46, y + 8, null)
                    y += 34
                }
            }
        }
    }

    companion object {
        val craftingBase: BufferedImage by lazy { RecipeImageGenerator.loadTexture("crafting_base.png") }
    }
}
