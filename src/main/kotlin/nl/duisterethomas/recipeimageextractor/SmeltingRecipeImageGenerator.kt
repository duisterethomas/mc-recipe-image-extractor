package nl.duisterethomas.recipeimageextractor

import net.minecraft.network.chat.Component
import java.awt.Graphics2D
import java.awt.image.BufferedImage


class SmeltingRecipeImageGenerator(
    recipeInfo: SmeltingRecipeInfo,
    icons: Map<ItemStackKey, BufferedImage>,
    labels: Map<Component, BufferedImage>?,
    labelWidths: Map<Component, Int>?
) : RecipeImageGenerator<SmeltingRecipeInfo>(
    recipeInfo, icons, labels, labelWidths,
    baseImage = smeltingBase,
    baseWidth = 178,
    height = if (labels != null) 196 else 124,
    labelsStartY = 122
) {
    override fun Graphics2D.drawSlots() {
        drawImage(icons[recipeInfo.slot.uniqueKey]!!, 10, 10, null)
        drawImage(icons[recipeInfo.output.uniqueKey]!!, 130, 46, null)
    }

    companion object {
        val smeltingBase: BufferedImage by lazy { loadTexture("smelting_base.png") }
    }
}