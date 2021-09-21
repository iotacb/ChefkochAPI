package de.kostari.chefkoch.recipe.json

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import de.kostari.chefkoch.recipe.data.RecipeData

class JsonParser {

    companion object {
        private var builder: GsonBuilder = GsonBuilder()
        private var gson: Gson = builder.setPrettyPrinting().create()

        fun toJson(recipe: RecipeData): String {
            return gson.toJson(recipe)
        }
    }

}