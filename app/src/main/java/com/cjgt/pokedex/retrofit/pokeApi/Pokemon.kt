package com.cjgt.pokedex.retrofit.pokeApi

import com.google.gson.annotations.SerializedName
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Pokemon (
    @PrimaryKey
    @SerializedName("id")
    var id: Int = 0,

    @SerializedName("name")
    var name: String = "",

    @SerializedName("height")
    var height: Int = 0,

    @SerializedName("weight")
    var weight: Int = 0,

    @SerializedName("types")
    var types: List<Type> = emptyList(),

    @SerializedName("sprites")
    var sprites: Sprites = Sprites(""),

    @SerializedName("stats")
    var stats: List<Stat> = emptyList()
)

data class Sprites(
    @SerializedName("front_default")
    var frontDefault: String?
)

data class Type(
    @SerializedName("slot")
    val slot: Int,
    @SerializedName("type")
    val type: TypeInfo
)

data class TypeInfo(
    @SerializedName("name")
    val name: String
)

data class Stat(
    @SerializedName("base_stat")
    val baseStat: Int,

    @SerializedName("stat")
    val statInfo: StatInfo
)

data class StatInfo(
    @SerializedName("name")
    val name: String
)