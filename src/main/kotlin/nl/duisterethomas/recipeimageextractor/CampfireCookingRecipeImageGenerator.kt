package nl.duisterethomas.recipeimageextractor

import net.minecraft.network.chat.Component
import java.awt.Graphics2D
import java.awt.image.BufferedImage


class CampfireCookingRecipeImageGenerator(
    recipeInfo: CampfireCookingRecipeInfo,
    icons: Map<ItemStackKey, BufferedImage>,
    labels: Map<Component, BufferedImage>?,
    labelWidths: Map<Component, Int>?
) : RecipeImageGenerator<CampfireCookingRecipeInfo>(
    recipeInfo, icons, labels, labelWidths,
    baseImage = campfireCookingBase,
    baseWidth = 178,
    height = if (labels != null) 156 else 84,
    labelsStartY = 82
) {
    override fun Graphics2D.drawSlots() {
        drawImage(icons[recipeInfo.slot.uniqueKey]!!, 10, 10, null)
        drawImage(icons[recipeInfo.output.uniqueKey]!!, 130, 24, null)
    }

    companion object {
        val campfireCookingBase: BufferedImage by lazy { loadTexture("campfire_cooking_base.png") }
    }
}