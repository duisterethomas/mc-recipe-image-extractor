package net.bloople.recipeimagesexporter

import net.minecraft.item.ItemStack
import java.awt.image.BufferedImage
import java.lang.Integer.max
import javax.imageio.ImageIO


class CampfireCookingRecipeImageGenerator(
    private val recipeInfo: CampfireCookingRecipeInfo,
    private val itemsData: ItemsData
) : RecipeImageGenerator {
    override val width = max(166 + rightImage.width, recipeInfo.items.maxOf { itemsData.itemNameWidths[it]!! } + 46 + 8)
    override val height = baseImage.height

    override fun generate(imageWidth: Int, imageHeight: Int): BufferedImage {
        return BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB).apply {
            raster.setRect(0, 0, baseImage.getData(0, 0, width, height))
            raster.setRect(width - 2, 0, rightImage.getData(0, 0, 2, height))

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
        private const val campfireCookingLeftImage = "iVBORw0KGgoAAAANSUhEUgAABAAAAAB6BAMAAADTxTwEAAAAAXNSR0IB2cksfwAAAARnQU1BAACxjwv8YQUAAAAgY0hSTQAAeiYAAICEAAD6AAAAgOgAAHUwAADqYAAAOpgAABdwnLpRPAAAABVQTFRFNzc3W1tbi4uLm5ubxsbG29vb////re9hcwAAAAlwSFlzAAALEwAACxMBAJqcGAAAAAd0SU1FB+oLBxUdMFJUpscAAAFBSURBVHja7dfBCcMwEERRtZAWtgP3sB2EtOD+S8hdOCB0sjPv3+eyPBAaH0U3nCAdQCs5AAAQAAJAACgVwJir5fn2UgDoNgBq6lyeby8FgB4HoAAAAIBkAAUAAAAkAygAAAAgGUABAAAAyQAKAAAASAZQAAAAQAqAugoAAABIBlAAAACAJwAAAADwDQQAAACiADQAAACQC6ABAACAXAANAAAA5AJoAAAAIBdAAwAAAH8HoM651fn+UgDoLgAEgAAQAAJAAAgAASAABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgAAQAAJAAAgAASAABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgAAQAAJAAAgAASAABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgAAQAPoN4P1ScuNwg+i+Mj+dKpXg3r8AAAAASUVORK5CYII="
        private val baseImage = ImageIO.read(decodeBase64(campfireCookingLeftImage)).asARGB()
        private val rightImage = ImageIO.read(decodeBase64(CraftingRecipeImageGenerator.craftingRightImage)).asARGB()
    }
}
