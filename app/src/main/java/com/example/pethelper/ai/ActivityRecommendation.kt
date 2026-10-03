package com.example.pethelper.ai

import com.example.pethelper.db.entity.Activity
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.json.JSONObject

class ActivityRecommendation(apiKey: String) {
    private val model = GenerativeModel(
        "gemini-3.5-flash",
        apiKey, generationConfig {
            responseMimeType = "application/json"
        }
    )

    fun getActivityRecommendation(
        breed: String, weight: String,
        birthday: String, neutered: String,
        activityLevel: String, favoriteActivities: String, limitations: String,
        goals: String, petId: Int, animal: String
    ): Flow<List<Activity>> = flow {
        val prompt = """
            You're a vet rehabilitation specialist and expert pet trainer. 
            Provide a list of activities suitable for the following $animal:
            Breed: $breed, Weight: $weight, Birthday date: $birthday, neutered: $neutered, activity level: $activityLevel,
            favorite activities: $favoriteActivities, physical limitations: $limitations, goals to achieve: $goals
            Requirements for the activity plan:
            1. Take into account all physical limitations and health features
            2. Sort activities in a logical, safe order (e.g. warm-up -> main activity -> cool-down/mental training
            3. Include duration as physical time (e.g. "10 minutes") or repetitions (e.g. "15 times")
            4. Provide useful notes for each activity (e.g. muscle training, mind training, flexibility)
            5. Take into account all goals to achieve
            Return the response STRICTLY as a JSON array of objects:
            {
               "items": [{"name": "Warm-up walk", "duration": "5 minutes", "notes": "Joint mobility"},
               {"name": "Fetching", "duration": "10 minutes", "notes": "Cardio and endurance"}]
            }
        """.trimIndent()

        val response = model.generateContent(prompt)
        val activityItemsJson = JSONObject(
            response.text ?: throw Exception("No response from AI")
        ).optJSONArray("items")
        val activityItems = mutableListOf<Activity>()
        if (activityItemsJson != null) {
            for (i in 0 until activityItemsJson.length()) {
                val activity = activityItemsJson.getJSONObject(i)
                activityItems.add(
                    Activity(
                        activity.getString("name"),
                        activity.getString("duration"),
                        activity.getString("notes"),
                        petId
                    )
                )
            }
        }
        emit(activityItems)

    }.flowOn(Dispatchers.IO)

}