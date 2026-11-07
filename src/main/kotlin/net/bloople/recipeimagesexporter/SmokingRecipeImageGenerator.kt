package net.bloople.recipeimagesexporter

import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.registry.Registries
import java.awt.image.BufferedImage
import java.lang.Integer.max
import javax.imageio.ImageIO


class SmokingRecipeImageGenerator(
    private val recipeInfo: SmokingRecipeInfo,
    private val itemsData: ItemsData
) : RecipeImageGenerator {
    override val width = max(178 + rightImage.width, recipeInfo.items.maxOf { itemsData.itemNameWidths[it]!! } + 46 + 8)
    override val height = baseImage.height

    override fun generate(imageWidth: Int, imageHeight: Int): BufferedImage {
        return  BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB).apply {
            raster.setRect(0, 0, baseImage.getData(0, 0, width, height))
            raster.setRect(width - 2, 0, rightImage.getData(0, 0, 2, height))

            createGraphics().use {
                drawImage(itemsData.iconImage(recipeInfo.slot), 10, 10, null)
                val coalStack = ItemStack(Registries.ITEM.get(Identifier.of("coal")), 1)
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
        private const val smokingLeftImage = "iVBORw0KGgoAAAANSUhEUgAABAAAAADCBAMAAAAlXK0cAAAAAXNSR0IB2cksfwAAAARnQU1BAACxjwv8YQUAAAAgY0hSTQAAeiYAAICEAAD6AAAAgOgAAHUwAADqYAAAOpgAABdwnLpRPAAAABhQTFRFNzc3Pz8/W1tbi4uLm5ubxsbG29vb////2Wv9NAAAAAlwSFlzAAALEwAACxMBAJqcGAAAAAd0SU1FB+oLBxUoMh5rBV0AAAJcSURBVHja7dzbbYNAEEBRWqCFtDAdpIftIEoLbj+WeQiTADY/ETPn+iFkiZ/dI3aJFLpvla4zBNUBNFUOAAAEgAAQAKoKoFsXB2f1955/MJIA6LoAYtXtYP5f+EUAKCuA9lgB7p9+Ph7ew2GPAwBKDeBx0Z9e0/F02FsRADCw1QCMi8G4LAgAJb8N7Fff84UfAABU4zawLe/9xkNLAAAGtcQmUAAIAAEgAASAABAAAkAACAABIAAEgDID6LYKAwuACgCIjSwhAAgAAaBLAIjbOgAAAKAOgNMnAwAAAHUABAAAAFAZQAAAAAA5AcT2hX4BIAAAAIDKAAIAAABIuQmMxWcHQAAAAACVAQQAAACQcBMYo4KnGf5zdgEAAIDKAAIAAABIBmCY1ojV33stAQAAUATA/B3hNhAAACoCiFcANAAAACDnJnChYBNAAwAAAPIBaAsAbQdAAwAAAPICiAMADQAAAEi8CYz9TWADAAAAci4B504GAAAAAAAAAAAuDeDso14BAACAFAA8KBIAAAAAAAAAAHhjAn8/Yva9R80KAF0ZgAAQAAJAAAgAASAABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgADQfwII/9oFAACFAQgAASAABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgAAQAAJAAAgAASAABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgAAQAAJAAAgAASAABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgAAQAAJAAAgAASAAtA3g60OV6z6NQel+AB3ruJUNHZMaAAAAAElFTkSuQmCC"
        private val baseImage = ImageIO.read(decodeBase64(smokingLeftImage)).asARGB()
        private val rightImage = ImageIO.read(decodeBase64(CraftingRecipeImageGenerator.craftingRightImage)).asARGB()
    }
}
