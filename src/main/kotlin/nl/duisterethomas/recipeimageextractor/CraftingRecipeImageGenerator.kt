package nl.duisterethomas.recipeimageextractor

import net.minecraft.network.chat.Component
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import kotlin.math.max


class CraftingRecipeImageGenerator(
    recipeInfo: CraftingRecipeInfo,
    icons: Map<ItemStackKey, BufferedImage>,
    labels: Map<Component, BufferedImage>?,
    labelWidths: Map<Component, Int>?
) : RecipeImageGenerator<CraftingRecipeInfo>(
    recipeInfo, icons, labels, labelWidths,
    baseImage = craftingBase,
    baseWidth = 214,
    height = 122 + recipeBottom.height + if (labels != null) {
        val labelCount = recipeInfo.itemStacks.distinctBy { it.uniqueKey }.size
        8 + (labelCount * 30) + (max(labelCount - 1, 0) * 4)
    } else 0,
    labelsStartY = 122
) {
    override fun Graphics2D.drawSlots() {
        if(recipeInfo.slot1 != null) drawImage(icons[recipeInfo.slot1.uniqueKey]!!, 10, 10, null)
        if(recipeInfo.slot2 != null) drawImage(icons[recipeInfo.slot2.uniqueKey]!!, 46, 10, null)
        if(recipeInfo.slot3 != null) drawImage(icons[recipeInfo.slot3.uniqueKey]!!, 82, 10, null)
        if(recipeInfo.slot4 != null) drawImage(icons[recipeInfo.slot4.uniqueKey]!!, 10, 46, null)
        if(recipeInfo.slot5 != null) drawImage(icons[recipeInfo.slot5.uniqueKey]!!, 46, 46, null)
        if(recipeInfo.slot6 != null) drawImage(icons[recipeInfo.slot6.uniqueKey]!!, 82, 46, null)
        if(recipeInfo.slot7 != null) drawImage(icons[recipeInfo.slot7.uniqueKey]!!, 10, 82, null)
        if(recipeInfo.slot8 != null) drawImage(icons[recipeInfo.slot8.uniqueKey]!!, 46, 82, null)
        if(recipeInfo.slot9 != null) drawImage(icons[recipeInfo.slot9.uniqueKey]!!, 82, 82, null)
        drawImage(icons[recipeInfo.output.uniqueKey]!!, 166, 46, null)
    }

    companion object {
        val craftingBase: BufferedImage by lazy { loadTexture("crafting_base.png") }
    }
}