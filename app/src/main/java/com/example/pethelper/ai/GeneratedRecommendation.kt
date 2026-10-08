package com.example.pethelper.ai

import com.example.pethelper.R
import com.example.pethelper.data.entity.*
import com.example.pethelper.ui.viewModel.PetFields
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.json.JSONArray
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class GeneratedRecommendation(apiKey: String) {
    private val model = GenerativeModel(
        "gemini-3.8-flash",
        apiKey, generationConfig {
            responseMimeType = "application/json"
        },
        requestOptions = RequestOptions(timeout = 15.seconds)
    )

    private val fallbackModel = GenerativeModel(
        "gemini-3.5-flash-lite",
        apiKey, generationConfig {
            responseMimeType = "application/json"
        },
        requestOptions = RequestOptions(timeout = 15.seconds)
    )

    private suspend fun chooseModel(prompt: String): GenerateContentResponse {
        return try {

            model.generateContent(prompt)
        } catch (e: Exception) {
            delay(300.milliseconds)
            return fallbackModel.generateContent(prompt)
        }
    }

    fun getActivityRecommendation(
        pet: PetFields
    ): Flow<List<Activity>> = flow {
        val prompt = """
            You're a vet rehabilitation specialist and expert pet trainer. 
            Provide a list of activities suitable for the following ${pet.animal}:
            Breed: ${pet.breed}, Weight: ${pet.weight}, Birthday date: ${pet.birthday}, neutered: ${pet.neutered}, activity level: ${pet.activityLevel},
            favorite activities: ${pet.favActivities}, physical limitations: ${pet.limitations}, goals to achieve: ${pet.goals}
            Requirements for the activity plan:
            1. Take into account all physical limitations and health features
            2. Sort activities in a logical, safe order (e.g. warm-up -> main activity -> cool-down/mental training
            3. Include duration as physical time (e.g. "10 minutes") or repetitions (e.g. "15 times")
            4. Provide useful notes for each activity (e.g. muscle training, mind training, flexibility)
            5. Take into account all goals to achieve
            6. Choose a suitable category for the activity from the following list only:
            "Ball", "Frisbee", "Tugging", "Ball_of_yarn", "Wand_toy", "Warm_up", "Cool_down", "Cat_tree",
            "Mind_training", "Learning_tricks", "Walking", "Swimming"
            Return the response STRICTLY as a top-level JSON array of objects:
               [{"name": "Warm-up walk", "duration": "5 minutes", "notes": "Joint mobility", "category": "Warm_up"},
               {"name": "Fetching", "duration": "10 minutes", "notes": "Cardio and endurance", "category": "Ball"}]
        """.trimIndent()

        val response = chooseModel(prompt)
        val activityItemsJson = JSONArray(
            response.text ?: throw Exception("No response from AI")
        )
        val activityItems = mutableListOf<Activity>()
        for (i in 0 until activityItemsJson.length()) {
            val activity = activityItemsJson.getJSONObject(i)
            val icon = getDrawableActivity(activity.optString("category", ""))
            activityItems.add(
                Activity(
                    activity.getString("name"),
                    activity.getString("duration"),
                    activity.getString("notes"),
                    icon,
                    pet.id
                )
            )
        }
        emit(activityItems)

    }.flowOn(Dispatchers.IO)

    fun getNutritionRecommendation(
        pet: PetFields, allergies: List<OtherNutrition>, supplements: List<OtherNutrition>
    ): Flow<List<FoodItem>> = flow {
        val allergiesName =
            if (allergies.isEmpty()) "No allergies" else allergies.joinToString { it.toString() }
        val supplementsName =
            if (supplements.isEmpty()) "No supplements" else supplements.joinToString { it.toString() }
        val prompt = """
            You're a vet nutritionist. Provide a list of food items suitable for the following ${pet.animal} (only natural food, no dry pet food etc):
            Breed: ${pet.breed}, Weight: ${pet.weight}, Birthday date: ${pet.birthday}, neutered: ${pet.neutered}, Allergies: $allergiesName, Supplements which the pet takes: $supplementsName
            For the unit use grams, cups, oz, lb, kg (choose the most suitable unit from this list), 
            you can also add short notes for the product (1-5 words) if you want so.
            Also choose a suitable category for the product from the following list only:
            "Meat","Offal", "Bones", "Fish","Chicken","Fruits","Vegetables","Berries","Eggs","Dairy","Water".
            Return the response STRICTLY as a top-level JSON array of objects:
               [{"name": "ground beef", "portionSize": "200", "unit": "g", "notes": null, "category": "Meat"},
               {"name": "vegetables", "portionSize": "50", "unit": "g", "notes": "Pumpkin, zucchini", "category": "Vegetables"}]
        """.trimIndent()

        val response = chooseModel(prompt)
        val foodItemsJson = JSONArray(
            response.text ?: throw Exception("No response from AI")
        )
        val foodItems = mutableListOf<FoodItem>()
        for (i in 0 until foodItemsJson.length()) {
            val foodItem = foodItemsJson.getJSONObject(i)
            val icon = getDrawableNutrition(foodItem.optString("category", ""))
            foodItems.add(
                FoodItem(
                    foodItem.getString("name"),
                    foodItem.optString("notes", ""),
                    foodItem.get("portionSize").toString(),
                    foodItem.getString("unit"),
                    icon,
                    pet.id
                )
            )
        }
        emit(foodItems)

    }.flowOn(Dispatchers.IO)

    private fun getDrawableNutrition(category: String): Int {
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

    private fun getDrawableActivity(category: String): Int {
        return when (category) {
            "Ball" -> R.drawable.ball
            "Frisbee" -> R.drawable.frisbee
            "Tugging" -> R.drawable.dog_toy
            "Ball_of_yarn" -> R.drawable.roll
            "Wand_toy" -> R.drawable.cat_toy
            "Warm_up" -> R.drawable.warm_up
            "Cool_down" -> R.drawable.cool_down
            "Cat_tree" -> R.drawable.cat_tree
            "Mind_training" -> R.drawable.mind_training
            "Learning_tricks" -> R.drawable.clicker
            "Walking" -> R.drawable.walking
            "Swimming" -> R.drawable.swimming
            else -> R.drawable.ball
        }
    }
}