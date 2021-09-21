package de.kostari.chefkoch.recipe.data

data class RecipeDate(
    val day: Int,
    val month: Int,
    val year: Int,

    val hour: Int = 0,
    val minute: Int = 0
) {
    override fun toString(): String = "$day.$month.$year" + (if (hour != 0 && minute != 0) " $hour:$minute" else "")
}
