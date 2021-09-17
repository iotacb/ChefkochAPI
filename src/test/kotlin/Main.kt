import de.kostari.chefkoch.ChefkochAPI
import org.junit.jupiter.api.Test

class Main {

    @Test
    fun main() {
        val api = ChefkochAPI()
        val r = api.readRecipe("https://www.chefkoch.de/rezepte/476931141659490/Brittas-Raeuberfleisch.html?zufall=on")
        r.getIngredients().forEach { println(it.toString()) }
    }

}