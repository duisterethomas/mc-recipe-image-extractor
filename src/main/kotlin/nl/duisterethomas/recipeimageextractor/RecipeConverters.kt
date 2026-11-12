package nl.duisterethomas.recipeimageextractor

import net.minecraft.core.HolderLookup
import net.minecraft.recipebook.PlaceRecipe
import net.minecraft.world.item.crafting.BlastingRecipe
import net.minecraft.world.item.crafting.CampfireCookingRecipe
import net.minecraft.world.item.crafting.CraftingRecipe
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.RecipeHolder
import net.minecraft.world.item.crafting.SmeltingRecipe
import net.minecraft.world.item.crafting.SmithingRecipe
import net.minecraft.world.item.crafting.SmithingRecipeInput
import net.minecraft.world.item.crafting.SmithingTransformRecipe
import net.minecraft.world.item.crafting.SmithingTrimRecipe
import net.minecraft.world.item.crafting.SmokingRecipe
import net.minecraft.world.item.crafting.StonecutterRecipe
import net.minecraft.world.item.ItemStack


class CraftingRecipeAdapter(val slots: Array<Array<ItemStack>?> = arrayOfNulls(9)) : PlaceRecipe<Ingredient> {
    override fun addItemToSlot(
        inputs: Ingredient,
        slot: Int,
        amount: Int,
        gridX: Int,
        gridY: Int
    ) {
        val itemStacks = inputs.items
        if(itemStacks.isNotEmpty()) slots[slot] = itemStacks
    }
}

fun convertCraftingRecipe(recipe: RecipeHolder<CraftingRecipe>, provider: HolderLookup.Provider): List<CraftingRecipeInfo> {
    val recipePath = "crafting/${recipe.id.namespace}/${recipe.id.path}"

    val ingredients = recipe.value().ingredients

    val adapter = CraftingRecipeAdapter()
    adapter.placeRecipe(3, 3, -1, recipe, ingredients.iterator(),0)

    val maxStacksSize = ingredients.maxOfOrNull { it.items.size } ?: 0
    val result = recipe.value().getResultItem(provider)

    return List(maxStacksSize) { index ->
        CraftingRecipeInfo(
            recipePath,
            if(maxStacksSize > 1) "${recipePath}_$index" else recipePath,
            adapter.slots[0]?.getOrLast(index),
            adapter.slots[1]?.getOrLast(index),
            adapter.slots[2]?.getOrLast(index),
            adapter.slots[3]?.getOrLast(index),
            adapter.slots[4]?.getOrLast(index),
            adapter.slots[5]?.getOrLast(index),
            adapter.slots[6]?.getOrLast(index),
            adapter.slots[7]?.getOrLast(index),
            adapter.slots[8]?.getOrLast(index),
            result
        )
    }
}

fun convertSmeltingRecipe(recipe: RecipeHolder<SmeltingRecipe>, provider: HolderLookup.Provider): List<SmeltingRecipeInfo> {
    val recipePath = "smelting/${recipe.id.namespace}/${recipe.id.path}"

    val items = recipe.value().ingredients[0].items
    val result = recipe.value().getResultItem(provider)

    return items.mapIndexed { index, stack ->
        SmeltingRecipeInfo(
            recipePath,
            if(items.size > 1) "${recipePath}_$index" else recipePath,
            stack,
            result
        )
    }
}

fun convertBlastingRecipe(recipe: RecipeHolder<BlastingRecipe>, provider: HolderLookup.Provider): List<BlastingRecipeInfo> {
    val recipePath = "blasting/${recipe.id.namespace}/${recipe.id.path}"

    val items = recipe.value().ingredients[0].items
    val result = recipe.value().getResultItem(provider)

    return items.mapIndexed { index, stack ->
        BlastingRecipeInfo(
            recipePath,
            if(items.size > 1) "${recipePath}_$index" else recipePath,
            stack,
            result
        )
    }
}

fun convertSmokingRecipe(recipe: RecipeHolder<SmokingRecipe>, provider: HolderLookup.Provider): List<SmokingRecipeInfo> {
    val recipePath = "smoking/${recipe.id.namespace}/${recipe.id.path}"

    val items = recipe.value().ingredients[0].items
    val result = recipe.value().getResultItem(provider)

    return items.mapIndexed { index, stack ->
        SmokingRecipeInfo(
            recipePath,
            if(items.size > 1) "${recipePath}_$index" else recipePath,
            stack,
            result
        )
    }
}

fun convertCampfireCookingRecipe(recipe: RecipeHolder<CampfireCookingRecipe>, provider: HolderLookup.Provider): List<CampfireCookingRecipeInfo> {
    val recipePath = "campfire_cooking/${recipe.id.namespace}/${recipe.id.path}"

    val items = recipe.value().ingredients[0].items
    val result = recipe.value().getResultItem(provider)

    return items.mapIndexed { index, stack ->
        CampfireCookingRecipeInfo(
            recipePath,
            if(items.size > 1) "${recipePath}_$index" else recipePath,
            stack,
            result
        )
    }
}

fun convertStonecuttingRecipe(recipe: RecipeHolder<StonecutterRecipe>, provider: HolderLookup.Provider): List<StonecuttingRecipeInfo> {
    val recipePath = "stonecutting/${recipe.id.namespace}/${recipe.id.path}"

    val items = recipe.value().ingredients[0].items
    val result = recipe.value().getResultItem(provider)

    return items.mapIndexed { index, stack ->
        StonecuttingRecipeInfo(
            recipePath,
            if(items.size > 1) "${recipePath}_$index" else recipePath,
            stack,
            result
        )
    }
}

fun convertSmithingRecipe(recipe: RecipeHolder<SmithingRecipe>, provider: HolderLookup.Provider): List<SmithingRecipeInfo> {
    val recipePath = "smithing/${recipe.id.namespace}/${recipe.id.path}"

    val value = recipe.value()
    val (template, base, addition) = when (value) {
        is SmithingTransformRecipe -> Triple(value.template, value.base, value.addition)
        is SmithingTrimRecipe -> Triple(value.template, value.base, value.addition)
        else -> return emptyList()
    }

    val maxStacksSize = arrayOf(template, base, addition).maxOf { it.items.size }

    return (0 until maxStacksSize).mapNotNull { index ->
        val templateStack = template.items.getOrLast(index)!!
        val baseStack = base.items.getOrLast(index)!!
        val additionStack = addition.items.getOrLast(index)!!

        val result = value.assemble(
            SmithingRecipeInput(
                templateStack,
                baseStack,
                additionStack
            ),
            provider
        )
        if (result.isEmpty) return@mapNotNull null

        SmithingRecipeInfo(
            recipePath,
            if(maxStacksSize > 1) "${recipePath}_$index" else recipePath,
            templateStack,
            baseStack,
            additionStack,
            result
        )
    }
}
