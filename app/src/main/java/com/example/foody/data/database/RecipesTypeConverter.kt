package com.example.foody.data.database

import androidx.room.TypeConverter
import com.example.foody.models.FoodRecipe
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RecipesTypeConverter {

    var gson = Gson()

    @TypeConverter
    fun foodRecipesToString(foodRecipe: FoodRecipe) : String {
        return gson.toJson(foodRecipe)
    }

    @TypeConverter
    fun stringToFood(data: String) : FoodRecipe {
        val listType = object : TypeToken<FoodRecipe>() {}.type
        return gson.fromJson(data, listType)
    }

}