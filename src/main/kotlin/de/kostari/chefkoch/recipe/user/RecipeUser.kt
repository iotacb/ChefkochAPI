package de.kostari.chefkoch.recipe.user

data class RecipeUser(
    val link: String,
    val image: String,
    val name: String,
) {
    override fun toString(): String = "$link, $image, $name"
}
