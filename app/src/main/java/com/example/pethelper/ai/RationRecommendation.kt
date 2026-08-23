package com.example.pethelper.ai

import com.example.pethelper.R
import com.example.pethelper.db.Allergy
import com.example.pethelper.db.FoodItem
import com.example.pethelper.db.Supplement
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.json.JSONObject

class RationRecommendation(apiKey: String, val petId: Int) {
    private val model = GenerativeModel(
        "gemini-3.6-flash",
        apiKey, generationConfig {
            responseMimeType = "application/json"
        }
    )

    fun getNutritionRecommendation(
        breed: String, weight: String, allergies: List<Allergy>,
        birthday: String, supplements: List<Supplement>, neutered: String
    ): Flow<List<FoodItem>> = flow {
        val allergiesName =
            if (allergies.isEmpty()) "No allergies" else allergies.joinToString { it.toString() }
        val supplementsName =
            if (supplements.isEmpty()) "No supplements" else supplements.joinToString { it.toString() }
        val prompt = """
            You're a vet nutritionist. Provide a list of food items suitable for the following pet (only natural food, no dry pet food etc):
            Breed: $breed, Weight: $weight, Birthday date: $birthday, neutered: $neutered, Allergies: $allergiesName, Supplements which the pet takes: $supplementsName
            For the unit use grams, cups, oz, lb, kg (choose the most suitable unit from this list), 
            you can also add short notes for the product (1-5 words) if you want so.
            Also choose a suitable category for the product from the following list only:
            "Meat","Offal", "Bones", "Fish","Chicken","Fruits","Vegetables","Berries","Eggs","Dairy","Water".
            Return the response strictly as a JSON array of objects:
            {
               "items": [{"name": "ground beef", "portionSize": "200", "unit": "g", "notes": null, "category": "Meat"},
               {"name": "vegetables", "portionSize": "50", "unit": "g", "notes": "Pumpkin, zucchini", "category": "Vegetables"}]
            }
        """.trimIndent()

        val response = model.generateContent(prompt)
        val foodItemsJson = JSONObject(
            response.text ?: throw Exception("No response from AI")
        ).optJSONArray("items")
        val foodItems = mutableListOf<FoodItem>()
        if (foodItemsJson != null) {
            for (i in 0 until foodItemsJson.length()) {
                val foodItem = foodItemsJson.getJSONObject(i)
                val icon = getDrawable(foodItem.optString("category", ""))
                foodItems.add(
                    FoodItem(
                        foodItem.getString("name"),
                        foodItem.optString("notes", ""),
                        foodItem.get("portionSize").toString(),
                        foodItem.getString("unit"),
                        icon,
                        petId
                    )
                )
            }
        }
        emit(foodItems)

    }.flowOn(Dispatchers.IO)

    private fun getDrawable(category: String): Int {
        return when (category) {
            "Meat" -> R.drawable.meat
            "Offal" -> R.drawable.liver
            "Bones" -> R.drawable.bone
            "Fish" -> R.drawable.fish
            "Chicken" -> R.drawable.chicken
            "Fruits" -> R.drawable.fruits
            "Vegetables" -> R.drawable.vegetables
            "Berries" -> R.drawable.berries
            "Eggs" -> R.drawable.egg
            "Dairy" -> R.drawable.diary
            "Water" -> R.drawable.water
            else -> R.drawable.meat
        }
    }
}
