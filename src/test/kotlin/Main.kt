import de.kostari.chefkoch.api.ChefkochAPI
import org.junit.jupiter.api.Test

class Main {

    @Test
    fun main() {
        val api = ChefkochAPI()
        // load and parse the data of a recipe
        val r = api.readRecipe("https://www.chefkoch.de/rezepte/3591611540048472/Pfannenbrot-ohne-Hefe.html?zufall=on")
        // parse the recipe data to a json file and print the content
        println(api.parseJson(r))

        // print all chefkoch recipe categories
        api.getCategories().forEach { println(it) }
    }

}