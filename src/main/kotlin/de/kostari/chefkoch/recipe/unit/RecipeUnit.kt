package de.kostari.chefkoch.recipe.unit

data class RecipeUnit(
    val ingredientWeight: Int,
    val ingredientUnit: RecipeUnits,
    val ingredient: String
) {
    override fun toString(): String = "${if(ingredientWeight != 0) "$ingredientWeight " else ""}${ingredientUnit.unitName}, $ingredient"
}
