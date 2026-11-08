package net.bloople.recipeimagesexporter

import net.minecraft.client.Minecraft
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.crafting.RecipeManager
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import java.awt.image.BufferedImage

interface RecipeInfo {
    val recipeBasePath: String
    val recipePath: String
    val itemStacks: List<ItemStack>
    val items: List<Item>
        get() {
            return itemStacks.map { it.item }.distinct()
        }
    fun imageGenerator(icons: Map<String, BufferedImage>, labels: Map<Item, BufferedImage>? = null, itemNameWidths: Map<Item, Int>? = null): RecipeImageGenerator
}

data class CraftingRecipeInfo(
    override val recipeBasePath: String,
    override val recipePath: String,
    val slot1: ItemStack?,
    val slot2: ItemStack?,
    val slot3: ItemStack?,
    val slot4: ItemStack?,
    val slot5: ItemStack?,
    val slot6: ItemStack?,
    val slot7: ItemStack?,
    val slot8: ItemStack?,
    val slot9: ItemStack?,
    val output: ItemStack
) : RecipeInfo {
    override val itemStacks: List<ItemStack>
        get() {
            return arrayOf(
                slot1,
                slot2,
                slot3,
                slot4,
                slot5,
                slot6,
                slot7,
                slot8,
                slot9,
                output
            ).filterNotNull().distinct()
        }

    override fun imageGenerator(icons: Map<String, BufferedImage>, labels: Map<Item, BufferedImage>?, itemNameWidths: Map<Item, Int>?): RecipeImageGenerator {
        return CraftingRecipeImageGenerator(this, icons, labels, itemNameWidths)
    }
}

data class StonecuttingRecipeInfo(
    override val recipeBasePath: String,
    override val recipePath: String,
    val slot: ItemStack,
    val output: ItemStack
) : RecipeInfo {
    override val itemStacks: List<ItemStack>
        get() = listOf(slot, output).distinct()

    override fun imageGenerator(icons: Map<String, BufferedImage>, labels: Map<Item, BufferedImage>?, itemNameWidths: Map<Item, Int>?): RecipeImageGenerator {
        return StonecuttingRecipeImageGenerator(this, icons, labels, itemNameWidths)
    }
}

data class SmeltingRecipeInfo(
    override val recipeBasePath: String,
    override val recipePath: String,
    val slot: ItemStack,
    val output: ItemStack
) : RecipeInfo {
    override val itemStacks: List<ItemStack>
        get() = listOf(slot, output).distinct()

    override fun imageGenerator(icons: Map<String, BufferedImage>, labels: Map<Item, BufferedImage>?, itemNameWidths: Map<Item, Int>?): RecipeImageGenerator {
        return SmeltingRecipeImageGenerator(this, icons, labels, itemNameWidths)
    }
}

data class BlastingRecipeInfo(
    override val recipeBasePath: String,
    override val recipePath: String,
    val slot: ItemStack,
    val output: ItemStack
) : RecipeInfo {
    override val itemStacks: List<ItemStack>
        get() = listOf(slot, output).distinct()

    override fun imageGenerator(icons: Map<String, BufferedImage>, labels: Map<Item, BufferedImage>?, itemNameWidths: Map<Item, Int>?): RecipeImageGenerator {
        return BlastingRecipeImageGenerator(this, icons, labels, itemNameWidths)
    }
}

data class SmokingRecipeInfo(
    override val recipeBasePath: String,
    override val recipePath: String,
    val slot: ItemStack,
    val output: ItemStack
) : RecipeInfo {
    override val itemStacks: List<ItemStack>
        get() = listOf(slot, output).distinct()

    override fun imageGenerator(icons: Map<String, BufferedImage>, labels: Map<Item, BufferedImage>?, itemNameWidths: Map<Item, Int>?): RecipeImageGenerator {
        return SmokingRecipeImageGenerator(this, icons, labels, itemNameWidths)
    }
}

data class CampfireCookingRecipeInfo(
    override val recipeBasePath: String,
    override val recipePath: String,
    val slot: ItemStack,
    val output: ItemStack
) : RecipeInfo {
    override val itemStacks: List<ItemStack>
        get() = listOf(slot, output).distinct()

    override fun imageGenerator(icons: Map<String, BufferedImage>, labels: Map<Item, BufferedImage>?, itemNameWidths: Map<Item, Int>?): RecipeImageGenerator {
        return CampfireCookingRecipeImageGenerator(this, icons, labels, itemNameWidths)
    }
}

data class SmithingRecipeInfo(
    override val recipeBasePath: String,
    override val recipePath: String,
    val slotTemplate: ItemStack,
    val slotBase: ItemStack,
    val slotAddition: ItemStack,
    val output: ItemStack
) : RecipeInfo {
    override val itemStacks: List<ItemStack>
        get() = listOf(slotTemplate, slotBase, slotAddition, output).distinct()

    override fun imageGenerator(icons: Map<String, BufferedImage>, labels: Map<Item, BufferedImage>?, itemNameWidths: Map<Item, Int>?): RecipeImageGenerator {
        return SmithingRecipeImageGenerator(this, icons, labels, itemNameWidths)
    }
}

data class RecipeGroup(
    val name: String,
    val subGroups: List<List<RecipeInfo>>
)

class RecipeInfos(recipeManager: RecipeManager) {
    val registryLookup = Minecraft.getInstance().level!!.registryAccess()

    private val craftingGroups = recipeManager.getAllRecipesFor(RecipeType.CRAFTING)
        .filterNot { it.value().isSpecial }.map { convertCraftingRecipe(it, registryLookup) }
    private val smeltingGroups = recipeManager.getAllRecipesFor(RecipeType.SMELTING)
        .filterNot { it.value().isSpecial }.map { convertSmeltingRecipe(it, registryLookup) }
    private val blastingGroups = recipeManager.getAllRecipesFor(RecipeType.BLASTING)
        .filterNot { it.value().isSpecial }.map { convertBlastingRecipe(it, registryLookup) }
    private val smokingGroups = recipeManager.getAllRecipesFor(RecipeType.SMOKING)
        .filterNot { it.value().isSpecial }.map { convertSmokingRecipe(it, registryLookup) }
    private val campfireCookingGroups = recipeManager.getAllRecipesFor(RecipeType.CAMPFIRE_COOKING)
        .filterNot { it.value().isSpecial }.map { convertCampfireCookingRecipe(it, registryLookup) }
    private val stonecuttingGroups = recipeManager.getAllRecipesFor(RecipeType.STONECUTTING)
        .filterNot { it.value().isSpecial }.map { convertStonecuttingRecipe(it, registryLookup) }
    private val smithingGroups = recipeManager.getAllRecipesFor(RecipeType.SMITHING)
        .filterNot { it.value().isSpecial }.map { convertSmithingRecipe(it, registryLookup) }

    val groups = listOf(
        RecipeGroup("Crafting", craftingGroups),
        RecipeGroup("Smelting", smeltingGroups),
        RecipeGroup("Blasting", blastingGroups),
        RecipeGroup("Smoking", smokingGroups),
        RecipeGroup("Campfire Cooking", campfireCookingGroups),
        RecipeGroup("Stonecutting", stonecuttingGroups),
        RecipeGroup("Smithing", smithingGroups)
    )

    val all = groups.flatMap { it.subGroups } .flatten()

    val items = all.flatMap { it.items }
        .distinctBy { BuiltInRegistries.ITEM.getKey(it) }
        .sortedBy { BuiltInRegistries.ITEM.getKey(it) }

    val itemStacks = (all.flatMap { it.itemStacks } + items.map { ItemStack(it) })
        .distinctBy { it.uniqueKey }
        .sortedBy { it.uniqueKey }
}
