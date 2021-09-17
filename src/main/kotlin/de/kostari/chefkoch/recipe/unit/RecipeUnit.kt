package de.kostari.chefkoch.recipe.unit

data class RecipeUnit(
    val ingredientWeight: String,
    val ingredientUnit: String,
    val ingredient: String
) {
    override fun toString(): String {
        return if (ingredientWeight.isEmpty() && ingredientUnit.isEmpty()) {
            ingredient
        } else {
            "$ingredientWeight${if(ingredientUnit.isEmpty()) "" else " $ingredientUnit"}, $ingredient"
        }
    }

    private fun String.isNumber(): Boolean = this.matches("-?\\d+(\\.\\d+)?".toRegex())
}
