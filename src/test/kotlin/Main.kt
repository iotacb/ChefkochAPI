import de.kostari.chefkoch.ChefkochAPI
import org.junit.jupiter.api.Test

class Main {

    @Test
    fun main() {
        val api = ChefkochAPI()
        val r = api.readRecipe("https://www.chefkoch.de/rezepte/597491159418023/Berliner-Krebbel-Pfannkuchen.html?zufall=on")
        r.getIngredients().forEach { i -> println(i.toString()) }
    }

}