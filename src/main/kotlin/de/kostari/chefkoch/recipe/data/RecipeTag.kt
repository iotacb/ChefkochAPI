package de.kostari.chefkoch.recipe.data

data class RecipeTag(
    val title: String,
    val link: String
) {
    override fun toString(): String = "$title, $link"
}
