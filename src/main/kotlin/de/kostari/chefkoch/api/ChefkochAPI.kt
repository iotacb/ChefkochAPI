package de.kostari.chefkoch.api

import de.kostari.chefkoch.recipe.Recipe
import de.kostari.chefkoch.recipe.json.JsonParser
import org.jsoup.Jsoup
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory

class ChefkochAPI {

    private val defaultFactory = SSLContext.getDefault().socketFactory

    /**
     * Reads all data from the provided link and parses it
     * to a recipe object
     *
     * @property link the link to the recipe
     * @property factory set a custom SSL factory, if empty it'll use a default factory
     * @return returns the recipe object of the parsed recipe
     */
    fun readRecipe(link: String, factory: SSLSocketFactory = defaultFactory) : Recipe {
        // load and parse the html of the categories site
        val document = Jsoup.connect(link).sslSocketFactory(factory).get()
        return Recipe(document)
    }

    /**
     * Updates the portions of a recipe and recalculates the ingredient amounts
     * of the recipe
     *
     * @property portion the amount of portions of the recipe
     * @property recipe the recipe whose portions are to be updated
     * @return returns the provided recipe with updated portions
     */
    fun updatePortions(portion: Int, recipe: Recipe): Recipe {
        recipe.getDataObject().portions = portion
        recipe.updatePortions(portion)
        return recipe
    }

    /**
     * Parses the recipe data object to the json format
     *
     * @property recipe the recipe which should be parsed
     * @return returns the parsed recipe
     */
    fun parseJson(recipe: Recipe): String {
        return JsonParser.toJson(recipe.getDataObject())
    }

    /**
     * Returns all categories of the chefkoch website
     * as an object in a list
     *
     * @return the categories in a list
     */
    fun getCategories(): List<ChefkochCategory> {
        val list = arrayListOf<ChefkochCategory>()
        // load and parse the html of the categories site
        val document = Jsoup.connect("${Constants.CHEFKOCH}/rezepte/kategorien/").sslSocketFactory(defaultFactory).get()

        val recipeContainer = document.select("div.content-box-padding.clearfix").first()!! // the html element which contains the categories
        val columns = recipeContainer.children().drop(0) // drop the first element of the column because it's not a category
        columns.forEach { // loop over all columns
            it.children().forEach { // loop over each item
                val child = it.child(0) // the item
                val title = child.text()
                val link = child.attr("href")

                list.add(ChefkochCategory(title, "${Constants.CHEFKOCH}$link"))
            }
        }
        return list
    }

}