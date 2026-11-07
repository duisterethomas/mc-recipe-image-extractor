package net.bloople.recipeimagesexporter

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import java.awt.image.BufferedImage
import java.lang.Integer.max
import javax.imageio.ImageIO


class SmeltingRecipeImageGenerator(
    private val recipeInfo: SmeltingRecipeInfo,
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
        private const val smeltingLeftImage = "iVBORw0KGgoAAAANSUhEUgAABAAAAADCBAMAAAAlXK0cAAAAAXNSR0IB2cksfwAAAARnQU1BAACxjwv8YQUAAAAgY0hSTQAAeiYAAICEAAD6AAAAgOgAAHUwAADqYAAAOpgAABdwnLpRPAAAABVQTFRFNzc3W1tbi4uLm5ubxsbG29vb////re9hcwAAAAlwSFlzAAALEwAACxMBAJqcGAAAAAd0SU1FB+oLBxUkIEFnOxkAAAIZSURBVHja7dzBTcQwFEVRt0ALrwN6cAeIFqb/EliAUAg4M5MNyv/nSpldNvZR7IwUj3e1bhiC7gCmOgcAAAJAAAgAdQUw9sXAAKBGALLrZmAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgAAQAAJAAAgAASAABIAAUF0AY5VPywBQBwBZZAkBQAAIAF0CQG77AAAAgD4ATt8MAAAA9AEQAAAAoDOAAAAAADUBZP2g3wAIAAAA0BlAAAAAgJKbwGyuAwABAAAAOgMIAAAAUHATmC8FP2b4z9kFAAAAOgMIAAAAUAzA57Qmu/97LQEAANAEwPdv4jUQAAA6AsgjACYAAABQcxO4UbAEMAEAAIB6AOYGwDwAMAEAAIC6AHIHwAQAAAAKbwJzvAmcAAAAQM0l4NzNAAAAAAAAAADApQGcPeoVAAAAKAHAQZEAAAAAAAAAAMATE/j7iNnnjpoVALoyAAEgAASAABAAAkAACAABIAAEgAAQAAJAAAgAASAABIAAEAACQAAIAAGg/wQQn3YBAEBjAAJAAAgAASAABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgAAQAAJAAAgAASAABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgAAQAAJAAAgAASAABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgAAQAAJAAAgArQG8vahz49UYtO4DuvEqxNL1igwAAAAASUVORK5CYII="
        private val baseImage = ImageIO.read(decodeBase64(smeltingLeftImage)).asARGB()
        private val rightImage = ImageIO.read(decodeBase64(CraftingRecipeImageGenerator.craftingRightImage)).asARGB()
    }
}
