package com.cjgt.pokedex.retrofit

import android.graphics.Color
import java.util.Locale

object TypeColors {
    private val typeColorMap = mapOf(
        "normal" to Color.parseColor("#A8A77A"),
        "fire" to Color.parseColor("#EE8130"),
        "fuego" to Color.parseColor("#EE8130"),
        "water" to Color.parseColor("#6390F0"),
        "agua" to Color.parseColor("#6390F0"),
        "electric" to Color.parseColor("#F7D02C"),
        "eléctrico" to Color.parseColor("#F7D02C"),
        "grass" to Color.parseColor("#7AC74C"),
        "tierra" to Color.parseColor("#7AC74C"),
        "ice" to Color.parseColor("#96D9D6"),
        "hielo" to Color.parseColor("#96D9D6"),
        "fighting" to Color.parseColor("#C22E28"),
        "lucha" to Color.parseColor("#C22E28"),
        "poison" to Color.parseColor("#A33EA1"),
        "veneno" to Color.parseColor("#A33EA1"),
        "ground" to Color.parseColor("#E2BF65"),
        "suelo" to Color.parseColor("#E2BF65"),
        "flying" to Color.parseColor("#A98FF3"),
        "volador" to Color.parseColor("#A98FF3"),
        "psychic" to Color.parseColor("#F95587"),
        "psíquico" to Color.parseColor("#F95587"),
        "bug" to Color.parseColor("#A6B91A"),
        "bicho" to Color.parseColor("#A6B91A"),
        "rock" to Color.parseColor("#B6A136"),
        "roca" to Color.parseColor("#B6A136"),
        "ghost" to Color.parseColor("#735797"),
        "fantasma" to Color.parseColor("#735797"),
        "dragon" to Color.parseColor("#6F35FC"),
        "dragón" to Color.parseColor("#6F35FC"),
        "dark" to Color.parseColor("#705746"),
        "siniestro" to Color.parseColor("#705746"),
        "steel" to Color.parseColor("#B7B7CE"),
        "acero" to Color.parseColor("#B7B7CE"),
        "fairy" to Color.parseColor("#D685AD"),
        "hada" to Color.parseColor("#D685AD")
    )

    fun getColorForType(type: String): Int {
        return typeColorMap[type.lowercase(Locale.ROOT)] ?: Color.parseColor("#000000")
    }
}