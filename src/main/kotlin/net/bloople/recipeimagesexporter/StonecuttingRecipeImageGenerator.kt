package net.bloople.recipeimagesexporter

import net.minecraft.item.ItemStack
import java.awt.image.BufferedImage
import java.lang.Integer.max
import javax.imageio.ImageIO


class StonecuttingRecipeImageGenerator(
    private val recipeInfo: StonecuttingRecipeInfo,
    private val itemsData: ItemsData
) : RecipeImageGenerator {
    override val width = max(168 + rightImage.width, recipeInfo.items.maxOf { itemsData.itemNameWidths[it]!! } + 46 + 8)
    override val height = baseImage.height

    override fun generate(imageWidth: Int, imageHeight: Int): BufferedImage {
        return BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB).apply {
            raster.setRect(0, 0, baseImage.getData(0, 0, width, height))
            raster.setRect(width - 2, 0, rightImage.getData(0, 0, 2, height))

            createGraphics().use {
                drawImage(itemsData.iconImage(recipeInfo.slot), 10, 46, null)
                drawImage(itemsData.iconImage(recipeInfo.output), 53, 21, null)
                drawImage(itemsData.iconImage(recipeInfo.output), 120, 46, null)

                var y = 94
                for(item in recipeInfo.items) {
                    drawImage(itemsData.iconImage(ItemStack(item, 1)), 8, y, null)
                    drawImage(itemsData.labelImage(item), 46, y + 8, null)
                    y += 34
                }
            }
        }
    }

    companion object {
        private const val stonecuttingLeftImage = "iVBORw0KGgoAAAANSUhEUgAABAAAAACmBAMAAAC2OeBQAAAAAXNSR0IB2cksfwAAAARnQU1BAACxjwv8YQUAAAAgY0hSTQAAeiYAAICEAAD6AAAAgOgAAHUwAADqYAAAOpgAABdwnLpRPAAAAB5QTFRFNzc3W1tbi4uLm5ubxsbG29vb////4MqfoJFyVEw7GeJbjAAAAAlwSFlzAAALEwAACxMBAJqcGAAAAAd0SU1FB+oLBxUtOIPJGAYAAAHKSURBVHja7dytDcJQFIbhK0jAssLZgA0QdwVWYAWQSOqwbIsgIfwUQnD0e15f8/VJmmPaNoqumSAdQFdyAAAgAASAABAAAkAACAABIAAEgAAQAMoDsG7PzcwFgHIAzHcP7Q8AAKAkAPvjfcMJAAAEgAAQAAJAAAgAASAABIAAEAACQABo8gDau8qmACgAQL1pa1MABIAAEAACQH8AYPSWAwAAAFIAjL1FAAB4AVAAAABAMoACAAAAkgEUAAAAkAFgMZwfugIoAAAAIALAyE+i6iYAAAAAmDaAseomAAAAAJg0gPoQAAAAkAygAAAAAJ8AAAAAwBkIAAAARAHoAAAAQC6ADgAAAOQC6AAAAEAugA4AAADkAugAAABALoAOwCQB1PY5AAAAIAfAL88BAAAAAAAAAAAAAAAAAGEAXi/Hry9IAaC/ByAABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgAAQAAJAAAgAASAABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgAAQAAJAAAgAASAABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgAAAwAYACAABIAAEgAAQAIoBsFoquWaC7C5ADBUom+UgIgAAAABJRU5ErkJggg=="
        private val baseImage = ImageIO.read(decodeBase64(stonecuttingLeftImage)).asARGB()
        private val rightImage = ImageIO.read(decodeBase64(CraftingRecipeImageGenerator.craftingRightImage)).asARGB()
    }
}
