package nl.duisterethomas.recipeimageextractor

import net.minecraft.network.chat.Component
import java.awt.Graphics2D
import java.awt.image.BufferedImage


class StonecuttingRecipeImageGenerator(
    recipeInfo: StonecuttingRecipeInfo,
    icons: Map<ItemStackKey, BufferedImage>,
    labels: Map<Component, BufferedImage>?,
    labelWidths: Map<Component, Int>?
) : RecipeImageGenerator<StonecuttingRecipeInfo>(
    recipeInfo, icons, labels, labelWidths,
    baseImage = stonecuttingBase,
    baseWidth = 168,
    height = if (labels != null) 160 else 88,
    labelsStartY = 86
) {
    override fun Graphics2D.drawSlots() {
        drawImage(icons[recipeInfo.slot.uniqueKey]!!, 10, 38, null)
        drawImage(icons[recipeInfo.output.uniqueKey]!!, 53, 12, null)
        drawImage(icons[recipeInfo.output.uniqueKey]!!, 120, 38, null)
    }

    companion object {
        val stonecuttingBase: BufferedImage by lazy { loadTexture("stonecutting_base.png") }
    }
}