package de.kostari.chefkoch.recipe

import de.kostari.chefkoch.recipe.misc.RecipeComment
import de.kostari.chefkoch.recipe.misc.RecipeData
import de.kostari.chefkoch.recipe.misc.RecipeDate
import de.kostari.chefkoch.recipe.misc.RecipeDifficulty
import de.kostari.chefkoch.recipe.unit.RecipeUnit
import de.kostari.chefkoch.recipe.unit.RecipeUnits
import de.kostari.chefkoch.recipe.user.RecipeUser
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class Recipe(private var recipeDocument: Document) {

    private lateinit var data: RecipeData

    init {
        updateRecipe(1)
    }

    /*
    Parses and updates all information about the given recipe
     */
    public fun updateRecipe(portion: Int) {
        val tmpLink = recipeDocument.location()
        var recipePortion = 1
        if (portion > 1) {
            recipePortion = portion
            recipeDocument = Jsoup.connect("$tmpLink${if (tmpLink.contains("?")) "&" else "?"}portionen=$recipePortion").get()
        }
        val recipeContainer = recipeDocument.select("main.ds-container.rds")
        val recipeHeader = recipeContainer.select("article.ds-box.ds-grid-float.ds-col-12.ds-col-m-8.recipe-header")
        val recipeInfoBlock = recipeHeader.select("div.ds-mb-right")
        val recipeCounts = recipeInfoBlock.select("div.ds-btn-box.recipe-meta-btns")
        val recipeRatingBlock = recipeCounts.select("a.recipe-rating-btn")
        val recipeInfo = recipeInfoBlock.select("small.ds-recipe-meta.recipe-meta")
        val recipeIngredientContainer = recipeContainer.select("article.ds-box.ds-grid-float.ds-col-12.ds-col-m-8.recipe-ingredients")

        val recipeNutritionContainer = recipeContainer.select("article.ds-box.ds-grid-float.ds-col-12.ds-col-m-8.recipe-nutrition")
        val recipeNutritionContent = recipeNutritionContainer.select("div.recipe-nutrition_content.ds-box.ds-grid").firstOrNull()

        val recipeInstructionsContainer = recipeContainer.select("article.ds-box.ds-grid-float.ds-col-12.ds-col-m-8.ds-or-3").first()!!

        val recipeCommentsContainer = recipeContainer.select("article#recipe-comments").first()!!

        val recipeLink = recipeDocument.location()
        val recipeTitle = recipeHeader.select("div.ds-mb.ds-mb-col").first()!!.child(0).text()
        val recipeDescription = recipeInfoBlock.select("p.recipe-text").text()
        val recipeRating = recipeRatingBlock.select("div.ds-rating-avg").select("strong").text()
        val recipeReviews = recipeRatingBlock.select("div.ds-rating-stars").attr("title")
        val recipeComments = recipeCounts.select("button.recipe-comments-anchor.rds-comment-ctn-btn").select("strong").text()
        val recipeTime = recipeInfo.select("span.recipe-preptime.rds-recipe-meta__badge").text()
        val recipeDifficulty = recipeInfo.select("span.recipe-difficulty.rds-recipe-meta__badge").text().trim()
        val recipeDate = recipeInfo.select("span.recipe-date.rds-recipe-meta__badge").text().trim()
        val recipeCalories = recipeInfo.select("span.recipe-kcalories.rds-recipe-meta__badge").text().trim()
        val recipeProtein = if (recipeNutritionContent == null) "0" else recipeNutritionContent.child(1).text().trim().split(" ")[1]
        val recipeFat = if (recipeNutritionContent == null) "0" else recipeNutritionContent.child(2).text().trim().split(" ")[1]
        val recipeCarbohydrates = if (recipeNutritionContent == null) "0" else recipeNutritionContent.child(3).text().trim().split(" ")[1]
        val recipeInstructions = recipeInstructionsContainer.child(2).text().trim()

        val recipeAuthorContainer = recipeInstructionsContainer.child(5).child(1)
        val recipeAuthorLink = recipeAuthorContainer.child(0).select("a").attr("href")
        val recipeAuthorImage = recipeAuthorContainer.child(0).select("a>amp-img").attr("src")
        val recipeAuthorName = recipeAuthorContainer.child(1).select("span").text()

        val recipeCommentWrapper = recipeCommentsContainer.select("div.bi-comment-forms")
        val recipeCommentsContent = recipeCommentWrapper.first()!!.child(12)

        data = RecipeData(
            link = recipeLink,
            title = recipeTitle,
            description = recipeDescription,
            rating = recipeRating.replace(",", ".").toFloat(),
            reviews = recipeReviews.split(" ")[0].toInt(),
            comments = recipeComments.toInt(),
            time = recipeTime.split(" ")[1].split(" ")[0].toInt(),
            difficulty = getDifficulty(recipeDifficulty),
            date = getDate(recipeDate),
            calories = if (recipeCalories.isNotEmpty()) recipeCalories.split(" ")[1].split(" ")[0].toFloat() else 0F,
            portions = recipePortion,
            ingredients = getIngredients(recipeIngredientContainer.select("table.ingredients.table-header").first()!!.child(0)),
            protein = recipeProtein.replace(",", ".").toFloat(),
            fat = recipeFat.replace(",", ".").toFloat(),
            carbohydrates = recipeCarbohydrates.replace(",", ".").toFloat(),
            instructions = recipeInstructions,
            author = RecipeUser(recipeAuthorLink, recipeAuthorImage, recipeAuthorName),
            commentsList = getComments(recipeCommentsContent)
        )
    }

    /*
    Will return the recipe difficulty
    returns NORMAL when the difficulty tag isn't matching with anything
     */
    private fun getDifficulty(diff: String): RecipeDifficulty {
        RecipeDifficulty.values().forEach {
            if (it.name.lowercase() == diff) {
                return it
            }
        }
        return RecipeDifficulty.NORMAL
    }

    /*
    Will return the date when the recipe got uploaded
     */
    private fun getDate(date: String): RecipeDate {
        val nums = date.replace("\uE916", "").trim().split(".")
        return RecipeDate(nums[0].toInt(), nums[1].toInt(), nums[2].toInt())
    }

    /*
    Returns all ingredients of the recipe
     */
    private fun getIngredients(element: Element): List<RecipeUnit> {
        val rows = element.children()
        val list = arrayListOf<RecipeUnit>()
        rows.forEach {
            val left = it.child(0)
            val right = it.child(1)
            var weight: String
            var unit: String
            if (left.text().trim().lowercase().contains("n. b.") || left.text().trim().lowercase().isNullOrEmpty()) {
                weight = "0"
                unit = "n. B."
            } else {
                weight = left.text().trim().split(" ")[0]
                unit = if(left.text().trim().contains(" ")) left.text().trim().split(" ")[1] else ""
            }
            val ingredient = right.text().trim()
            list.add(RecipeUnit(weight.toInt(), getUnit(unit), ingredient))
        }
        return list
    }

    /*
    Will return the weight unit of an ingredient by using the unit symbol
    returns GRAM when the unit tag isn't matching with anything
 */
    private fun getUnit(symbol: String): RecipeUnits {
        RecipeUnits.values().forEach {
            if (it.unitName.lowercase() == symbol.lowercase()) {
                return it
            }
        }
        return RecipeUnits.GRAM
    }

    private fun getComments(element: Element): List<RecipeComment> {
        val commentChildren = element.children()
        val list = arrayListOf<Element>()
        val commentsList = arrayListOf<RecipeComment>()

        commentChildren.forEach {
            if (it.className().contains("comment-item")) {
                list.add(it)
            }
        }

        list.forEach {
            val left = it.child(0)
            val right = it.child(1)
            val link = left.select("div>a").attr("href")
            val image = left.select("div>a>amp-img").attr("src")
            val name = right.select("strong>a").text().trim()
            val text = right.child(2).text().trim()
            val date = right.select("div>small").text().trim().split(" ")[0]
            val dates = date.split(".")
            val time = right.select("div>small").text().trim().split(" ")[1]
            val user = RecipeUser(link, image, name)
            commentsList.add(RecipeComment(user, text, RecipeDate(dates[0].toInt(), dates[1].toInt(), dates[2].toInt(), time.split(":")[0].toInt(), time.split(":")[1].toInt())))
        }
        return commentsList
    }

    fun getDataObject(): RecipeData {
        return data
    }

    fun getLink(): String {
        return data.link
    }

    fun getTitle(): String {
        return data.title
    }

    fun getDescription(): String {
        return data.description
    }

    fun getReviews(): Int {
        return data.reviews
    }

    fun getComments(): Int {
        return data.comments
    }

    fun getTime(): Int {
        return data.time
    }

    fun getDifficulty(): RecipeDifficulty {
        return data.difficulty
    }

    fun getDate(): RecipeDate {
        return data.date
    }

    fun getCalories(): Float {
        return data.calories
    }

    fun getIngredients(): List<RecipeUnit> {
        return data.ingredients
    }

    fun getProtein(): Float {
        return data.protein
    }

    fun getFat(): Float {
        return data.fat
    }

    fun getCarbohydrates(): Float {
        return data.carbohydrates
    }

    fun getInstructions(): String {
        return data.instructions
    }

    fun getAuthor(): RecipeUser {
        return data.author
    }

    fun getCommentsList(): List<RecipeComment> {
        return data.commentsList
    }

}