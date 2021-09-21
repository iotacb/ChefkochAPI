package de.kostari.chefkoch.recipe.data

data class RecipeImage(
    val link: String,
    val name: String,
    val image: String,
) {
    override fun toString(): String = "$name, $link: $image"
}