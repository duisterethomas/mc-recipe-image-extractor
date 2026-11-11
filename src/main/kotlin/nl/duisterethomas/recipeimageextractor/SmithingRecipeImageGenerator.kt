package nl.duisterethomas.recipeimageextractor

import net.minecraft.network.chat.Component
import java.awt.Graphics2D
import java.awt.image.BufferedImage


class SmithingRecipeImageGenerator(
    recipeInfo: SmithingRecipeInfo,
    icons: Map<ItemStackKey, BufferedImage>,
    labels: Map<Component, BufferedImage>?,
    labelWidths: Map<Component, Int>?
) : RecipeImageGenerator<SmithingRecipeInfo>(
    recipeInfo, icons, labels, labelWidths,
    baseImage = smithingBase,
    baseWidth = 230,
    height = if (labels != null) 192 else 52,
    labelsStartY = 50
) {
    override fun Graphics2D.drawSlots() {
        drawImage(icons[recipeInfo.slotTemplate.uniqueKey]!!, 10, 10, null)
        drawImage(icons[recipeInfo.slotBase.uniqueKey]!!, 46, 10, null)
        drawImage(icons[recipeInfo.slotAddition.uniqueKey]!!, 82, 10, null)
        drawImage(icons[recipeInfo.output.uniqueKey]!!, 190, 10, null)
    }

    companion object {
        val smithingBase: BufferedImage by lazy { loadTexture("smithing_base.png") }
    }
}