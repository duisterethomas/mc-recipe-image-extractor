package net.bloople.recipeimagesexporter

import net.minecraft.client.MinecraftClient
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.recipe.RecipeManager
import net.minecraft.recipe.RecipeType

interface RecipeInfo {
    val recipeBasePath: String
    val recipePath: String
    val itemStacks: List<ItemStack>
    val items: List<Item>
        get() {
            return itemStacks.map { it.item }.distinct()
        }
    fun imageGenerator(itemsData: ItemsData): RecipeImageGenerator
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

    override fun imageGenerator(itemsData: ItemsData): RecipeImageGenerator {
        return CraftingRecipeImageGenerator(this, itemsData)
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

    override fun imageGenerator(itemsData: ItemsData): RecipeImageGenerator {
        return StonecuttingRecipeImageGenerator(this, itemsData)
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

    override fun imageGenerator(itemsData: ItemsData): RecipeImageGenerator {
        return SmeltingRecipeImageGenerator(this, itemsData)
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

    override fun imageGenerator(itemsData: ItemsData): RecipeImageGenerator {
        return BlastingRecipeImageGenerator(this, itemsData)
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

    override fun imageGenerator(itemsData: ItemsData): RecipeImageGenerator {
        return SmokingRecipeImageGenerator(this, itemsData)
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

    override fun imageGenerator(itemsData: ItemsData): RecipeImageGenerator {
        return CampfireCookingRecipeImageGenerator(this, itemsData)
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

    override fun imageGenerator(itemsData: ItemsData): RecipeImageGenerator {
        return SmithingRecipeImageGenerator(this, itemsData)
    }
}

class RecipeInfos(recipeManager: RecipeManager) {
    val registryLookup = MinecraftClient.getInstance().world!!.registryManager

    private val craftingGroups = recipeManager.listAllOfType(RecipeType.CRAFTING)
        .filterNot { it.value().isIgnoredInRecipeBook }.map { convertCraftingRecipe(it, registryLookup) }
    private val smeltingGroups = recipeManager.listAllOfType(RecipeType.SMELTING)
        .filterNot { it.value().isIgnoredInRecipeBook }.map { convertSmeltingRecipe(it, registryLookup) }
    private val blastingGroups = recipeManager.listAllOfType(RecipeType.BLASTING)
        .filterNot { it.value().isIgnoredInRecipeBook }.map { convertBlastingRecipe(it, registryLookup) }
    private val smokingGroups = recipeManager.listAllOfType(RecipeType.SMOKING)
        .filterNot { it.value().isIgnoredInRecipeBook }.map { convertSmokingRecipe(it, registryLookup) }
    private val campfireCookingGroups = recipeManager.listAllOfType(RecipeType.CAMPFIRE_COOKING)
        .filterNot { it.value().isIgnoredInRecipeBook }.map { convertCampfireCookingRecipe(it, registryLookup) }
    private val stonecuttingGroups = recipeManager.listAllOfType(RecipeType.STONECUTTING)
        .filterNot { it.value().isIgnoredInRecipeBook }.map { convertStonecuttingRecipe(it, registryLookup) }
    private val smithingGroups = recipeManager.listAllOfType(RecipeType.SMITHING)
        .filterNot { it.value().isIgnoredInRecipeBook }.map { convertSmithingRecipe(it, registryLookup) }

    val groups = listOf(
        craftingGroups,
        smeltingGroups,
        blastingGroups,
        smokingGroups,
        campfireCookingGroups,
        stonecuttingGroups,
        smithingGroups
    ).flatten()

    val all = groups.flatten()

    val items = all.flatMap { it.items }.distinctBy { it.identifier }.sortedBy { it.identifier }

    val itemStacks = (all.flatMap { it.itemStacks } + items.map { ItemStack(it) }).distinctBy { it.uniqueKey }
        .sortedBy { it.uniqueKey }
}
