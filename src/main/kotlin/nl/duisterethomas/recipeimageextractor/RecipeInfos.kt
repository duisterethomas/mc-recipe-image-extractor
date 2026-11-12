package nl.duisterethomas.recipeimageextractor

import net.minecraft.network.chat.Component
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeHolder
import net.minecraft.world.item.crafting.RecipeInput
import net.minecraft.world.level.Level
import java.awt.image.BufferedImage

interface RecipeInfo {
    val recipeBasePath: String
    val recipePath: String
    val itemStacks: List<ItemStack>
    fun imageGenerator(icons: Map<ItemStackKey, BufferedImage>, labels: Map<Component, BufferedImage>? = null, labelWidths: Map<Component, Int>? = null): RecipeImageGenerator<*>
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

    override fun imageGenerator(icons: Map<ItemStackKey, BufferedImage>, labels: Map<Component, BufferedImage>?, labelWidths: Map<Component, Int>?): RecipeImageGenerator<CraftingRecipeInfo> {
        return CraftingRecipeImageGenerator(this, icons, labels, labelWidths)
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

    override fun imageGenerator(icons: Map<ItemStackKey, BufferedImage>, labels: Map<Component, BufferedImage>?, labelWidths: Map<Component, Int>?): RecipeImageGenerator<StonecuttingRecipeInfo> {
        return StonecuttingRecipeImageGenerator(this, icons, labels, labelWidths)
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

    override fun imageGenerator(icons: Map<ItemStackKey, BufferedImage>, labels: Map<Component, BufferedImage>?, labelWidths: Map<Component, Int>?): RecipeImageGenerator<SmeltingRecipeInfo> {
        return SmeltingRecipeImageGenerator(this, icons, labels, labelWidths)
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

    override fun imageGenerator(icons: Map<ItemStackKey, BufferedImage>, labels: Map<Component, BufferedImage>?, labelWidths: Map<Component, Int>?): RecipeImageGenerator<BlastingRecipeInfo> {
        return BlastingRecipeImageGenerator(this, icons, labels, labelWidths)
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

    override fun imageGenerator(icons: Map<ItemStackKey, BufferedImage>, labels: Map<Component, BufferedImage>?, labelWidths: Map<Component, Int>?): RecipeImageGenerator<SmokingRecipeInfo> {
        return SmokingRecipeImageGenerator(this, icons, labels, labelWidths)
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

    override fun imageGenerator(icons: Map<ItemStackKey, BufferedImage>, labels: Map<Component, BufferedImage>?, labelWidths: Map<Component, Int>?): RecipeImageGenerator<CampfireCookingRecipeInfo> {
        return CampfireCookingRecipeImageGenerator(this, icons, labels, labelWidths)
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

    override fun imageGenerator(icons: Map<ItemStackKey, BufferedImage>, labels: Map<Component, BufferedImage>?, labelWidths: Map<Component, Int>?): RecipeImageGenerator<SmithingRecipeInfo> {
        return SmithingRecipeImageGenerator(this, icons, labels, labelWidths)
    }
}

data class RecipeGroup(
    val name: String,
    val subGroups: List<List<RecipeInfo>>
)

class RecipeInfos(private val level: Level, private val namespace: String? = null) {
    val registryLookup = level.registryAccess()

    private fun <I : RecipeInput, T : Recipe<I>> recipesFor(type: RecipeType<T>): List<RecipeHolder<T>> =
        level.recipeManager.getAllRecipesFor(type)
            .filterNot { it.value().isSpecial }
            .filter { namespace == null || it.id.namespace == namespace }

    private val craftingGroups = recipesFor(RecipeType.CRAFTING).map { convertCraftingRecipe(it, registryLookup) }
    private val smeltingGroups = recipesFor(RecipeType.SMELTING).map { convertSmeltingRecipe(it, registryLookup) }
    private val blastingGroups = recipesFor(RecipeType.BLASTING).map { convertBlastingRecipe(it, registryLookup) }
    private val smokingGroups = recipesFor(RecipeType.SMOKING).map { convertSmokingRecipe(it, registryLookup) }
    private val campfireCookingGroups = recipesFor(RecipeType.CAMPFIRE_COOKING).map { convertCampfireCookingRecipe(it, registryLookup) }
    private val stonecuttingGroups = recipesFor(RecipeType.STONECUTTING).map { convertStonecuttingRecipe(it, registryLookup) }
    private val smithingGroups = recipesFor(RecipeType.SMITHING).map { convertSmithingRecipe(it, registryLookup) }

    val groups = listOf(
        RecipeGroup("Crafting", craftingGroups),
        RecipeGroup("Smelting", smeltingGroups),
        RecipeGroup("Blasting", blastingGroups),
        RecipeGroup("Smoking", smokingGroups),
        RecipeGroup("Campfire Cooking", campfireCookingGroups),
        RecipeGroup("Stonecutting", stonecuttingGroups),
        RecipeGroup("Smithing", smithingGroups)
    ).filter { it.subGroups.isNotEmpty() }

    val all = groups.flatMap { it.subGroups } .flatten()

    val itemStacks = (all.flatMap { it.itemStacks } + all.flatMap { it.itemStacks }.map { it.copyWithCount(1) })
        .distinctBy { it.uniqueKey }
        .sortedBy { it.uniqueKey.item.toString() }
}