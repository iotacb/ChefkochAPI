import de.kostari.chefkoch.ChefkochAPI
import org.junit.jupiter.api.Test

class Main {

    @Test
    fun main() {
        val api = ChefkochAPI()
        val r = api.readRecipe("https://www.chefkoch.de/rezepte/3591611540048472/Pfannenbrot-ohne-Hefe.html?zufall=on")
        r.updatePortions(3)
        println(r.toString())
    }

}