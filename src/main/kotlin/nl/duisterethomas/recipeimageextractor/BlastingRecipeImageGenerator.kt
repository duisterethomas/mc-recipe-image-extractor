package nl.duisterethomas.recipeimageextractor

import net.minecraft.network.chat.Component
import java.awt.Graphics2D
import java.awt.image.BufferedImage


class BlastingRecipeImageGenerator(
    recipeInfo: BlastingRecipeInfo,
    icons: Map<ItemStackKey, BufferedImage>,
    labels: Map<Component, BufferedImage>?,
    labelWidths: Map<Component, Int>?
) : RecipeImageGenerator<BlastingRecipeInfo>(
    recipeInfo, icons, labels, labelWidths,
    baseImage = blastingBase,
    baseWidth = 178,
    height = if (labels != null) 196 else 124,
    labelsStartY = 122
) {
    override fun Graphics2D.drawSlots() {
        drawImage(icons[recipeInfo.slot.uniqueKey]!!, 10, 10, null)
        drawImage(icons[recipeInfo.output.uniqueKey]!!, 130, 46, null)
    }

    companion object {
        val blastingBase: BufferedImage by lazy { loadTexture("blasting_base.png") }
    }
}