package de.kostari.chefkoch.recipe.misc

data class RecipeTag(
    val title: String,
    val link: String
) {
    override fun toString(): String = "$title, $link"
}
