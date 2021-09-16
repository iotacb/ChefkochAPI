package de.kostari.chefkoch.recipe.misc

import de.kostari.chefkoch.recipe.unit.RecipeUnit
import de.kostari.chefkoch.recipe.user.RecipeUser

data class RecipeData(

    /*
    Main information about the recipe
     */
    val link: String,
    val title: String,
    val description: String,
    val rating: Float,
    val reviews: Int,
    val comments: Int,
    val time: Int,
    val difficulty: RecipeDifficulty,
    val date: RecipeDate,
    val calories: Float,

    /*
    Information about the ingredients
     */
    var portions: Int = 1,
    val ingredients: List<RecipeUnit>,

    /*
    Information about the nutritional values
     */
    val protein: Float,
    val fat: Float,
    val carbohydrates: Float,

    val instructions: String,

    val author: RecipeUser,

    val commentsList: List<RecipeComment>

)