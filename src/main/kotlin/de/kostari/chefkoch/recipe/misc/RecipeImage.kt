package de.kostari.chefkoch.recipe.misc

data class RecipeImage(
    val link: String,
    val name: String,
    val image: String,
) {
    override fun toString(): String = "$name, $link: $image"
}