package de.kostari.chefkoch.recipe

import de.kostari.chefkoch.api.Constants
import de.kostari.chefkoch.recipe.data.*
import de.kostari.chefkoch.recipe.data.RecipeUnit
import de.kostari.chefkoch.recipe.data.RecipeUser
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class Recipe(private var recipeDocument: Document) {

    private lateinit var data: RecipeData

    init {
        updateRecipe(1)
    }

    /**
     * Updates the portions of a recipe and recalculates the ingredient amounts
     * of the recipe
     *
     * @property portion the amount of portions of the recipe
     * @return returns the provided recipe with updated portions
     */
    fun updatePortions(portion: Int) {
        updateRecipe(portion)
    }

    /**
     * Parses and updates the recipe
     *
     * @property portion the amount of portions of the recipe
     */
    private fun updateRecipe(portion: Int) {
        val tmpLink = recipeDocument.location()
        var recipePortion = 1
        if (portion > 1) {
            recipePortion = portion
            recipeDocument =
                Jsoup.connect("$tmpLink${if (tmpLink.contains("?")) "&" else "?"}portionen=$recipePortion").get()
        }
        val recipeContainer = recipeDocument.select("main.ds-container.rds")
        val recipeHeader = recipeContainer.select("article.ds-box.ds-grid-float.ds-col-12.ds-col-m-8.recipe-header")
        val recipeInfoBlock = recipeHeader.select("div.ds-mb-right")
        val recipeCounts = recipeInfoBlock.select("div.ds-btn-box.recipe-meta-btns")
        val recipeRatingBlock = recipeCounts.select("a.recipe-rating-btn")
        val recipeInfo = recipeInfoBlock.select("small.ds-recipe-meta.recipe-meta")
        val recipeIngredientContainer =
            recipeContainer.select("article.ds-box.ds-grid-float.ds-col-12.ds-col-m-8.recipe-ingredients")

        val recipeNutritionContainer =
            recipeContainer.select("article.ds-box.ds-grid-float.ds-col-12.ds-col-m-8.recipe-nutrition")
        val recipeNutritionContent =
            recipeNutritionContainer.select("div.recipe-nutrition_content.ds-box.ds-grid").firstOrNull()

        val recipeInstructionsContainer =
            recipeContainer.select("article.ds-box.ds-grid-float.ds-col-12.ds-col-m-8.ds-or-3").first()!!

        val recipeCommentsContainer = recipeContainer.select("article#recipe-comments").first()!!

        val recipeLink = recipeDocument.location()
        val recipeTitle = recipeHeader.select("div.ds-mb.ds-mb-col").first()!!.child(0).text()
        val recipeCarouselContainer = recipeHeader.select("div.ds-mb-left.recipe-image").first()!!
        val recipeDescription = recipeInfoBlock.select("p.recipe-text").text()
        val recipeRating = recipeRatingBlock.select("div.ds-rating-avg").select("strong").text()
        val recipeReviews = recipeRatingBlock.select("div.ds-rating-stars").attr("title")
        val recipeComments =
            recipeCounts.select("button.recipe-comments-anchor.rds-comment-ctn-btn").select("strong").text()
        val recipeTime = recipeInfo.select("span.recipe-preptime.rds-recipe-meta__badge").text()
        val recipeDifficulty =
            recipeInfo.select("span.recipe-difficulty.rds-recipe-meta__badge").text().trim().split(" ")[1]
        val recipeDate = recipeInfo.select("span.recipe-date.rds-recipe-meta__badge").text().trim()
        val recipeCalories = recipeInfo.select("span.recipe-kcalories.rds-recipe-meta__badge").text().trim()
        val recipeProtein =
            if (recipeNutritionContent == null) "0" else recipeNutritionContent.child(1).text().trim().split(" ")[1]
        val recipeFat =
            if (recipeNutritionContent == null) "0" else recipeNutritionContent.child(2).text().trim().split(" ")[1]
        val recipeCarbohydrates =
            if (recipeNutritionContent == null) "0" else recipeNutritionContent.child(3).text().trim().split(" ")[1]
        val recipeInstructions = recipeInstructionsContainer.child(2).text().trim()

        val recipeTagsContainer = recipeInstructionsContainer.child(4)

        val recipeAuthorContainer = recipeInstructionsContainer.child(5).child(1)
        val recipeAuthorLink = recipeAuthorContainer.child(0).select("a").attr("href")
        val recipeAuthorImage = recipeAuthorContainer.child(0).select("a>amp-img").attr("src")
        val recipeAuthorName = recipeAuthorContainer.child(1).select("span").text()

        val recipeCommentWrapper = recipeCommentsContainer.select("div.bi-comment-forms")
        val recipeCommentsContent = recipeCommentWrapper.first()!!.child(12)

        println(recipeDifficulty)

        data = RecipeData(
            link = recipeLink,
            title = recipeTitle,
            description = recipeDescription,
            rating = recipeRating.replace(",", ".").toFloat(),
            reviews = recipeReviews.split(" ")[0].replace(".", "").replace(",", "").toInt(),
            comments = recipeComments.replace(".", "").replace(",", "").toInt(),
            time = recipeTime.split(" ")[1].split(" ")[0].toInt(),
            difficulty = getDifficulty(recipeDifficulty),
            date = getDate(recipeDate),
            calories = (if (recipeCalories.isNotEmpty()) recipeCalories.split(" ")[1].split(" ")[0].toFloat() else 0F) * recipePortion,
            portions = recipePortion,
            ingredients = getIngredients(recipeIngredientContainer.first()!!),
            protein = recipeProtein.replace(",", ".").toFloat() * recipePortion,
            fat = recipeFat.replace(",", ".").toFloat() * recipePortion,
            carbohydrates = recipeCarbohydrates.replace(",", ".").toFloat() * recipePortion,
            instructions = recipeInstructions,
            author = RecipeUser(recipeAuthorLink, recipeAuthorImage, recipeAuthorName),
            commentsList = getComments(recipeCommentsContent),
            images = getRecipeImages(recipeCarouselContainer),
            tags = getRecipeTags(recipeTagsContainer)
        )
    }

    /**
     * Converts the string difficulty to a difficulty object
     *
     * @property diff the difficulty which should be converted
     * @return the converted difficulty
     */
    private fun getDifficulty(diff: String): RecipeDifficulty {
        RecipeDifficulty.values().forEach { // loop over each difficulty
            if (it.diffName.equals(diff, true)) { // check if the difficulty's name matches the provided name
                return it
            }
        }
        return RecipeDifficulty.NORMAL // return normal difficulty if nothing matches
    }

    /**
     * Converts a string date to a date object
     * and returns it
     *
     * @property date the date which should be converted
     * @return the converted date
     */
    private fun getDate(date: String): RecipeDate {
        val nums = date.replace("\uE916", "").trim().split(".") // remove unicode character
        return RecipeDate(nums[0].toInt(), nums[1].toInt(), nums[2].toInt())
    }

    /**
     * Returns all ingredients of the recipe
     *
     * @property element the container of the ingredients table
     * @return a list of all ingredients
     */
    private fun getIngredients(element: Element): List<RecipeUnit> {
        val list = arrayListOf<RecipeUnit>()
        val tables = element.select("table.ingredients.table-header")
        tables.forEach {
            val table = if (it.childrenSize() > 1) it.child(1) else it.child(0)
            val ingredients = getIngredientsOfTable(table)
            list.addAll(ingredients)
        }
        return list
    }

    /**
     * Returns each ingredient in a table
     *
     * @property table the table whose content should be returned
     * @return a list of all ingredients in the provided table
     */
    private fun getIngredientsOfTable(table: Element): List<RecipeUnit> {
        val list = arrayListOf<RecipeUnit>()
        val rows = table.children()
        rows.forEach {
            val left = it.child(0).text().trim() // amount
            val ingredientName = it.child(1).text().trim() // ingredient

            if (left.contains(" ")) {
                val splits = left.split(" ")
                var weight = ""
                var unit = ""
                if (splits.size == 2) {
                    val first = splits[0]
                    val second = splits[1]
                    if (first.isNumber()) {
                        weight = first
                        unit = second
                    } else {
                        if (isFractionSymbol(first)) {
                            weight = first
                            unit = second
                        } else {
                            weight = "$first $second"
                        }
                    }
                } else {
                    if (splits.size > 2) {
                        val first = splits[0]
                        val second = splits[1]
                        if (first.isNumber() && isFractionSymbol(second)) {
                            weight = "$first $second"
                            unit = listToString(splits, 2)
                        }
                    }
                }
                list.add(RecipeUnit(weight, unit, ingredientName))
            } else {
                var weight = ""
                var unit = ""
                if (left.isNumber()) {
                    weight = left
                } else {
                    if (left.isEmpty()) {
                        unit = left
                    } else {
                        weight = left
                    }
                }
                list.add(RecipeUnit(weight, unit, ingredientName))
            }
        }
        return list
    }

    /**
     * Returns each comment of the recipe
     *
     * @property element the container of the comment section
     * @return a list of all comments
     */
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
            commentsList.add(
                RecipeComment(
                    user,
                    text,
                    RecipeDate(
                        dates[0].toInt(),
                        dates[1].toInt(),
                        dates[2].toInt(),
                        time.split(":")[0].toInt(),
                        time.split(":")[1].toInt()
                    )
                )
            )
        }
        return commentsList
    }

    /**
     * Returns the link of each image of the recipes
     *
     * @property carouselContainer the container of the image carousel
     * @return a list of all image links
     */
    private fun getRecipeImages(carouselContainer: Element): List<RecipeImage> {
        val list = arrayListOf<RecipeImage>()
        val carousel = carouselContainer.select("amp-carousel#recipe-image-carousel").first()!!
        carousel.children().forEachIndexed { index, element ->
            if (index > 0 && index < carousel.childrenSize() - 1) {
                val child = element.child(0)
                if (child.attr("data-vars-type") != "video") {
                    val meta = element.child(1)
                    val image = child.child(0).attr("src")
                    val metaChild = meta.child(0)
                    val tmpLink = metaChild.attr("href")
                    val link = if (tmpLink.contains("chefkoch.de")) tmpLink else "${Constants.CHEFKOCH}$tmpLink"
                    list.add(RecipeImage(link, metaChild.text().trim(), image))
                }
            }
        }
        return list
    }

    /**
     * Returns all tags of the recipe
     *
     * @property tagContainer the container of the tags
     * @return a list of all tags
     */
    private fun getRecipeTags(tagContainer: Element): List<RecipeTag> {
        val list = arrayListOf<RecipeTag>()
        val carousel = tagContainer.child(0)
        carousel.children().forEach {
            val child = it.child(0)
            val title = child.text().trim()
            val tmpLink = child.attr("href")
            val link = if (tmpLink.contains("chefkoch.de")) tmpLink else "${Constants.CHEFKOCH}$tmpLink"
            list.add(RecipeTag(title, link))
        }
        return list
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

    fun getRating(): Float {
        return data.rating
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

    fun getPortions(): Int {
        return data.portions
    }

    fun getIngredients(): List<RecipeUnit> {
        return data.ingredients
    }

    /**
     * Combines all ingredients to a single string
     *
     * @return the combined ingredients
     */
    private fun getIngredientsString(): String {
        var string = ""
        getIngredients().forEachIndexed { index, it ->
            string += " - $it${if (index < getIngredients().size - 1) "\n" else ""}"
        }
        return string
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

    /**
     * Combines the recipe instructions to a single string
     *
     * @return the combined instructions
     */
    private fun getInstructionsListString(): String {
        var i = ""
        val instructions = data.instructions
        val lines = instructions.split("\n")
        lines.forEachIndexed { index, it ->
            i += " $it${if (index < lines.size - 1) "\n" else ""}"
        }
        return i
    }

    fun getAuthor(): RecipeUser {
        return data.author
    }

    fun getCommentsList(): List<RecipeComment> {
        return data.commentsList
    }

    /**
     * Combines all comments to a single string
     *
     * @return the combined comments
     */
    private fun getCommentsString(): String {
        var string = ""
        getCommentsList().forEachIndexed { index, it ->
            string += " - $it${if (index < getCommentsList().size - 1) "\n" else ""}"
        }
        return string
    }

    fun getImages(): List<RecipeImage> {
        return data.images
    }

    /**
     * Combines all image links to a single string
     *
     * @return the combined image links
     */
    private fun getImagesString(): String {
        var string = ""
        getImages().forEachIndexed { index, it ->
            string += " - $it${if (index < getImages().size - 1) "\n" else ""}"
        }
        return string
    }

    fun getTags(): List<RecipeTag> {
        return data.tags
    }

    /**
     * Combines the all tags to a single string
     *
     * @return the combined tags
     */
    private fun getTagsString(): String {
        var string = ""
        getTags().forEachIndexed { index, it ->
            string += " - $it${if (index < getTags().size - 1) "\n" else ""}"
        }
        return string
    }

    /**
     * Combines the content of a list to a string.
     * Change the range of to be concatenated items
     *
     * @property list the string whose content should be combined
     * @property start the start index from where the content should be concatenated
     * @property end the end index to whose index the string should be concatenated
     * @return returns the concatenated content of the provided list
     */
    private fun listToString(list: List<String>, start: Int = 0, end: Int = list.size): String {
        var string = ""
        list.forEachIndexed { i, s ->
            if (i in start..end) string += s
        }
        return string
    }

    /**
     * Checks if the provided string is a unicode fraction symbol
     *
     * @property input the string which should be checked
     * @return
     */
    private fun isFractionSymbol(input: String): Boolean {
        Fractions.fractions.forEach {
            if (input == it) {
                return true
            }
        }
        return false
    }

    private fun String.isNumber(): Boolean = this.matches("-?\\d+(\\.\\d+)?".toRegex())

    override fun toString(): String {
        val spacer = "--------\n"
        return "Link: ${getLink()}\n" +
                "Title: ${getTitle()}\n" +
                "Description: ${getDescription()}\n" +
                "Rating: ${getRating()}\n" +
                "Reviews: ${getReviews()}\n" +
                "Comments: ${getComments()}\n" +
                "Time: ${getTime()}\n" +
                "Difficulty: ${getDifficulty().name}\n" +
                "Date: ${getDate()}\n" +
                "Calories: ${getCalories()}\n" +
                "Portions: ${getPortions()}\n" +
                spacer +
                "Ingredients:\n${getIngredientsString()}\n" +
                spacer +
                "Protein: ${getProtein()}\n" +
                "Fat: ${getFat()}\n" +
                "Carbohydrates: ${getCarbohydrates()}\n" +
                "Instructions: ${getInstructionsListString()}" +
                "Author: ${getAuthor()}\n" +
                spacer +
                "Comments:\n${getCommentsString()}\n" +
                spacer +
                "Images:\n${getImagesString()}\n" +
                spacer +
                "Tags:\n${getTagsString()}"
    }

}