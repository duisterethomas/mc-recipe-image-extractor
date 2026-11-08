package net.bloople.recipeimagesexporter

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import java.awt.image.BufferedImage
import java.lang.Integer.max


class SmokingRecipeImageGenerator(
    private val recipeInfo: SmokingRecipeInfo,
    private val itemsData: ItemsData
) : RecipeImageGenerator {
    override val width = max(178 + RecipeImageGenerator.recipeRight.width, recipeInfo.items.maxOf { itemsData.itemNameWidths[it]!! } + 46 + 8)
    override val height = smokingBase.height

    override fun generate(imageWidth: Int, imageHeight: Int): BufferedImage {
        return  BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB).apply {
            raster.setRect(0, 0, smokingBase.getData(0, 0, width, height))
            raster.setRect(width - 2, 0, RecipeImageGenerator.recipeRight.getData(0, 0, 2, height))

            createGraphics().use {
                drawImage(itemsData.iconImage(recipeInfo.slot), 10, 10, null)
                val coalStack = ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace("coal")), 1)
                drawImage(itemsData.iconImage(coalStack), 10, 82, null)
                drawImage(itemsData.iconImage(recipeInfo.output), 130, 46, null)

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
        val smokingBase: BufferedImage by lazy { RecipeImageGenerator.loadTexture("smoking_base.png") }
    }
}
