package net.bloople.recipeimagesexporter

import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import java.awt.image.BufferedImage

data class ItemsData(
    val icons: Map<String, BufferedImage>,
    val labels: Map<Item, BufferedImage>,
    val itemNameWidths: Map<Item, Int>
) {
    fun iconImage(itemStack: ItemStack): BufferedImage {
        return icons[itemStack.uniqueKey]!!
    }

    fun labelImage(item: Item): BufferedImage {
        return labels[item]!!
    }
}
