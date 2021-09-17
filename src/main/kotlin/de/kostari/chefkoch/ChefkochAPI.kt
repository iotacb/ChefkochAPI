package de.kostari.chefkoch

import de.kostari.chefkoch.recipe.Recipe
import org.jsoup.Jsoup
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory

class ChefkochAPI {

    /*
    Reads the recipe from the provided link
    and returns a recipe object which contains all information about the recipe.
    But also contains a small amount of logic
     */
    fun readRecipe(link: String, factory: SSLSocketFactory = SSLContext.getDefault().socketFactory) : Recipe {
        val document = Jsoup.connect(link).sslSocketFactory(factory).get()
        return Recipe(document)
    }

    /*
    Reread the recipe with the provided amount of portions
     */
    fun updatePortions(portion: Int, recipe: Recipe): Recipe {
        recipe.getDataObject().portions = portion
        recipe.updatePortions(portion)
        return recipe
    }

}