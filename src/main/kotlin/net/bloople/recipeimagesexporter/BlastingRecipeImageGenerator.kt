package net.bloople.recipeimagesexporter

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import java.awt.image.BufferedImage
import java.lang.Integer.max
import javax.imageio.ImageIO


class BlastingRecipeImageGenerator(
    private val recipeInfo: BlastingRecipeInfo,
    private val itemsData: ItemsData
) : RecipeImageGenerator {
    override val width = max(178 + rightImage.width, recipeInfo.items.maxOf { itemsData.itemNameWidths[it]!! } + 46 + 8)
    override val height = baseImage.height

    override fun generate(imageWidth: Int, imageHeight: Int): BufferedImage {
        return BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB).apply {
            raster.setRect(0, 0, baseImage.getData(0, 0, width, height))
            raster.setRect(width - 2, 0, rightImage.getData(0, 0, 2, height))

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
        private const val blastingLeftImage = "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"
        private val baseImage = ImageIO.read(decodeBase64(blastingLeftImage)).asARGB()
        private val rightImage = ImageIO.read(decodeBase64(CraftingRecipeImageGenerator.craftingRightImage)).asARGB()
    }
}
