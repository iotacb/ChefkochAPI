# **ChefkochAPI**
[![](https://jitpack.io/v/iotacb/ChefkochAPI.svg)](https://jitpack.io/#iotacb/ChefkochAPI)
[![Awesome Kotlin Badge](https://kotlin.link/awesome-kotlin.svg)](https://github.com/KotlinBy/awesome-kotlin)

<p align="center">
    <img src="assets/logo.svg" alt="chefkochapi icon" width="60%"/>
</p>

## **Getting Started**

**ChefkochAPI** is distributed through [JitPack](https://jitpack.io/#iotacb/ChefkochAPI).

### **Adding the JitPack dependency**
To use the library you need to add the following repository and dependency to your ```build.gradle``` file of your module

```bash
repositories {
  maven { url "https://jitpack.io" }
}
```
```bash
dependencies {
    implementation 'com.github.iotacb:ChefkochAPI:1.0'
}
```

Please note that you should always use the latest version of the library. You can import the latest version automatically by using ```latest``` as the version numbe for the dependency.

### **Basic usage**
To use the library, create a instance of the ```ChefkochAPI.class``` like the following:
```kotlin
val api = ChefkochAPI()
```
then you can fetch a recipe by using the ```readRecipe``` function of the api class and store the result in a variable like the following:
```kotlin
val api = ChefkochAPI()
val recipe = api.readRecipe("LINK_OF_RECIPE")
```
this function returns a recipe object which contains all informations about the recipe.
If you want to calculate the recipe for different portions you can use the ```updatePortions``` function of the api class, or by calling the function directly of the recipe object like the following:

_via the api class_
```kotlin
val api = ChefkochAPI()
val recipe = api.readRecipe("LINK_OF_RECIPE")
// DO SOMETHING...
api.updatePortions(AMOUNT_OF_PORTIONS, recipe)
```
_via the recipe object_
```kotlin
val api = ChefkochAPI()
val recipe = api.readRecipe("LINK_OF_RECIPE")
// DO SOMETHING...
recipe.updatePortions(AMOUNT_OF_PORTIONS)
```

### **Use the data**
You can get informations about the recipe by using the getters of the recipe object like the following:
```kotlin
println(recipe.getTitle()) // Prints the title of the recipe
```

You can find a list of all getters here #COMING SOON#

### **Configuring**
You can change the used ```SSLFactory``` by passing a custom factory to the ```readRecipe``` function like the following:
```kotlin
val api = ChefkochAPI()
val recipe = api.readRecipe("LINK_OF_RECIPE", YOUR_SSL_FACTORY)
```

### **Libraries used**
**ChefkochAPI** uses **[Jsoup](https://jsoup.org)** to parse the html page of the recipe

## License
```markdown
MIT License

Copyright (c) 2021 Christopher Brandt (@iotacb)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

```
