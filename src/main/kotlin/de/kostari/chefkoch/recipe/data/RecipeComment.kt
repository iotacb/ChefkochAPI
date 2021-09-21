package de.kostari.chefkoch.recipe.data

data class RecipeComment(
    val user: RecipeUser,
    val comment: String,
    val date: RecipeDate
) {
    override fun toString(): String = "${user.link}, ${user.image}, ${user.name}, \n$comment,\n ${date.day}.${date.month}.${date.year} ${date.hour}:${date.minute}"
}