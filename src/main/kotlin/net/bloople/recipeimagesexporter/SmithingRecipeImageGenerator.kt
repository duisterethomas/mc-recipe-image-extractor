package net.bloople.recipeimagesexporter

import net.minecraft.item.ItemStack
import java.awt.image.BufferedImage
import java.lang.Integer.max
import javax.imageio.ImageIO


class SmithingRecipeImageGenerator(
    private val recipeInfo: SmithingRecipeInfo,
    private val itemsData: ItemsData
) : RecipeImageGenerator {
    override val width = max(230 + rightImage.width, recipeInfo.items.maxOf { itemsData.itemNameWidths[it]!! } + 46 + 8)
    override val height = baseImage.height

    override fun generate(imageWidth: Int, imageHeight: Int): BufferedImage {
        return BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB).apply {
            raster.setRect(0, 0, baseImage.getData(0, 0, width, height))
            raster.setRect(width - 2, 0, rightImage.getData(0, 0, 2, height))

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
        private const val smithingLeftImage = "iVBORw0KGgoAAAANSUhEUgAABAAAAAC+BAMAAABZvGCmAAAAAXNSR0IB2cksfwAAAARnQU1BAACxjwv8YQUAAAAgY0hSTQAAeiYAAICEAAD6AAAAgOgAAHUwAADqYAAAOpgAABdwnLpRPAAAABVQTFRF29vbm5ubW1tbxsbGNzc3i4uL////0wG7QQAAAAlwSFlzAAALEwAACxMBAJqcGAAAAAd0SU1FB+oLBxQ3I7hDQQYAAAG1SURBVHja7dfBCcMwDIbRdIWO0BW0QlfQCtl/hNxND8KUQPjfdxTIB/PA+DgkJVdKDgAABIAAEABKBfBd68lkcvzelgDQrQB66ZxMRgC2tgSAHgqgAQAAgGQADQAAACQDaAAAACAZQAMAAADJABoAAABIBtAAAABACoD+FQAAAJAMoAEAAABPAAAAAOAbCAAAAEQBKAAAACAXQAEAAAC5AAoAAADIBVAAAABALoACAAAAHgagz7XJZHL83pYA0J0ABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgAAQAAJAAAgAASAABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgAAQAAJAAAgAASAABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgAAQAAJAAAgAASAABIAAEAACQAAIAAEgAASAABAAAgAAdwCAABAAAkAACAABIAAEgAAQAAJAAAgAASAABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgAAQAAJAAAgAASAABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgADQvwG8PkrueLuD6C5KmfePgL4cRAAAAABJRU5ErkJggg=="
        private val baseImage = ImageIO.read(decodeBase64(smithingLeftImage)).asARGB()
        private val rightImage = ImageIO.read(decodeBase64(CraftingRecipeImageGenerator.craftingRightImage)).asARGB()
    }
}

