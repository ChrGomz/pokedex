package com.cjgt.pokedex.roomDB

import androidx.room.TypeConverter
import com.cjgt.pokedex.retrofit.pokeApi.Sprites
import com.cjgt.pokedex.retrofit.pokeApi.Stat
import com.cjgt.pokedex.retrofit.pokeApi.Type
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromTypeList(value: List<Type>?): String {
        val gson = Gson()
        val type = object : TypeToken<List<Type>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toTypeList(value: String): List<Type>? {
        val gson = Gson()
        val type = object : TypeToken<List<Type>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromSprites(value: Sprites?): String {
        val gson = Gson()
        return gson.toJson(value, Sprites::class.java)
    }

    @TypeConverter
    fun toSprites(value: String): Sprites? {
        val gson = Gson()
        return gson.fromJson(value, Sprites::class.java)
    }

    @TypeConverter
    fun fromStatList(value: List<Stat>?): String {
        val gson = Gson()
        val type = object : TypeToken<List<Stat>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toStatList(value: String): List<Stat>? {
        val gson = Gson()
        val type = object : TypeToken<List<Stat>>() {}.type
        return gson.fromJson(value, type)
    }
}