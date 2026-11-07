package net.bloople.recipeimagesexporter

import net.minecraft.item.ItemStack
import net.minecraft.recipe.*
import net.minecraft.registry.RegistryWrapper


class CraftingRecipeAdapter(val slots: Array<Array<ItemStack>?> = arrayOfNulls(9)) : RecipeGridAligner<Ingredient> {
    override fun acceptAlignedInput(
        inputs: Ingredient,
        slot: Int,
        amount: Int,
        gridX: Int,
        gridY: Int
    ) {
        val itemStacks = inputs.matchingStacks
        if(itemStacks.isNotEmpty()) slots[slot] = itemStacks
    }
}

fun convertCraftingRecipe(recipe: RecipeEntry<CraftingRecipe>, registryLookup: RegistryWrapper.WrapperLookup): List<CraftingRecipeInfo> {
    val recipePath = "crafting/${recipe.id.namespace}/${recipe.id.path}"

    val ingredients = recipe.value().ingredients

    val adapter = CraftingRecipeAdapter()
    adapter.alignRecipeToGrid(3, 3, -1, recipe, ingredients.iterator(),0)

    val maxStacksSize = ingredients.maxOfOrNull { it.matchingStacks.size } ?: 0

    val recipeInfos = ArrayList<CraftingRecipeInfo>()

    repeat(maxStacksSize) { index ->
        recipeInfos.add(
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
                recipe.value().getResult(registryLookup)
            )
        )
    }

    return recipeInfos
}

fun convertSmeltingRecipe(recipe: RecipeEntry<SmeltingRecipe>, registryLookup: RegistryWrapper.WrapperLookup): List<SmeltingRecipeInfo> {
    val recipePath = "smelting/${recipe.id.namespace}/${recipe.id.path}"

    val ingredient = recipe.value().ingredients[0]

    val recipeInfos = ArrayList<SmeltingRecipeInfo>()

    for((index, matchingStack) in ingredient.matchingStacks.withIndex()) {
        recipeInfos.add(SmeltingRecipeInfo(
            recipePath,
            if(ingredient.matchingStacks.size > 1) "${recipePath}_$index" else recipePath,
            matchingStack,
            recipe.value().getResult(registryLookup)
        ))
    }
    return recipeInfos
}

fun convertBlastingRecipe(recipe: RecipeEntry<BlastingRecipe>, registryLookup: RegistryWrapper.WrapperLookup): List<BlastingRecipeInfo> {
    val recipePath = "blasting/${recipe.id.namespace}/${recipe.id.path}"

    val ingredient = recipe.value().ingredients[0]

    val recipeInfos = ArrayList<BlastingRecipeInfo>()

    for((index, matchingStack) in ingredient.matchingStacks.withIndex()) {
        recipeInfos.add(BlastingRecipeInfo(
            recipePath,
            if(ingredient.matchingStacks.size > 1) "${recipePath}_$index" else recipePath,
            matchingStack,
            recipe.value().getResult(registryLookup)
        ))
    }
    return recipeInfos
}

fun convertSmokingRecipe(recipe: RecipeEntry<SmokingRecipe>, registryLookup: RegistryWrapper.WrapperLookup): List<SmokingRecipeInfo> {
    val recipePath = "smoking/${recipe.id.namespace}/${recipe.id.path}"

    val ingredient = recipe.value().ingredients[0]

    val recipeInfos = ArrayList<SmokingRecipeInfo>()

    for((index, matchingStack) in ingredient.matchingStacks.withIndex()) {
        recipeInfos.add(SmokingRecipeInfo(
            recipePath,
            if(ingredient.matchingStacks.size > 1) "${recipePath}_$index" else recipePath,
            matchingStack,
            recipe.value().getResult(registryLookup)
        ))
    }
    return recipeInfos
}

fun convertCampfireCookingRecipe(recipe: RecipeEntry<CampfireCookingRecipe>, registryLookup: RegistryWrapper.WrapperLookup): List<CampfireCookingRecipeInfo> {
    val recipePath = "campfire_cooking/${recipe.id.namespace}/${recipe.id.path}"

    val ingredient = recipe.value().ingredients[0]

    val recipeInfos = ArrayList<CampfireCookingRecipeInfo>()

    for((index, matchingStack) in ingredient.matchingStacks.withIndex()) {
        recipeInfos.add(CampfireCookingRecipeInfo(
            recipePath,
            if(ingredient.matchingStacks.size > 1) "${recipePath}_$index" else recipePath,
            matchingStack,
            recipe.value().getResult(registryLookup)
        ))
    }
    return recipeInfos
}

fun convertStonecuttingRecipe(recipe: RecipeEntry<StonecuttingRecipe>, registryLookup: RegistryWrapper.WrapperLookup): List<StonecuttingRecipeInfo> {
    val recipePath = "stonecutting/${recipe.id.namespace}/${recipe.id.path}"

    val ingredient = recipe.value().ingredients[0]

    val recipeInfos = ArrayList<StonecuttingRecipeInfo>()

    for((index, matchingStack) in ingredient.matchingStacks.withIndex()) {
        recipeInfos.add(StonecuttingRecipeInfo(
            recipePath,
            if(ingredient.matchingStacks.size > 1) "${recipePath}_$index" else recipePath,
            matchingStack,
            recipe.value().getResult(registryLookup)
        ))
    }
    return recipeInfos
}

fun convertSmithingRecipe(recipe: RecipeEntry<SmithingRecipe>, registryLookup: RegistryWrapper.WrapperLookup): List<SmithingRecipeInfo> {
    val recipePath = "smithing/${recipe.id.namespace}/${recipe.id.path}"

    val value = recipe.value()
    val (template, base, addition) = when (value) {
        is SmithingTransformRecipe -> Triple(value.template, value.base, value.addition)
        is SmithingTrimRecipe -> Triple(value.template, value.base, value.addition)
        else -> return emptyList()
    }

    val maxStacksSize = arrayOf(template, base, addition).maxOf { it.matchingStacks.size }

    val recipeInfos = ArrayList<SmithingRecipeInfo>()

    repeat(maxStacksSize) { index ->
        recipeInfos.add(
            SmithingRecipeInfo(
                recipePath,
                if(maxStacksSize > 1) "${recipePath}_$index" else recipePath,
                template.matchingStacks.getOrLast(index)!!,
                base.matchingStacks.getOrLast(index)!!,
                addition.matchingStacks.getOrLast(index)!!,
                value.getResult(registryLookup)
            )
        )
    }

    return recipeInfos
}
