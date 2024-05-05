package com.cjgt.pokedex.retrofit

import Pokemon
import android.util.Log
import com.cjgt.pokedex.retrofit.RetrofitClient.retrofitInstance


class PokemonRepository {
    private val pokemonApiService: PokemonApiService = retrofitInstance!!.create(
        PokemonApiService::class.java
    )

    suspend fun getPokemonById(id: Int): Pokemon? {
        return try {
            val response = pokemonApiService.getPokemonById(id)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            Log.e("API Error", e.message ?: "Unknown error")
            null
        }
    }

    suspend fun getPokemonByName(name: String): Pokemon? {
        return try {
            val response = pokemonApiService.getPokemonByName(name)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            Log.e("API Error", e.message ?: "Unknown error")
            null
        }
    }

    suspend fun getAllPokemon(limit: Int, sort: String): List<PokemonListItem>? {
        return try {
            val response = pokemonApiService.getAllPokemon(limit, sort)
            if (response.isSuccessful) response.body()?.results else null
        } catch (e: Exception) {
            Log.e("API Error", e.message ?: "Unknown error")
            null
        }
    }

    suspend fun getPokemonByUrl(url: String): Pokemon? {
        return try {
            val response = pokemonApiService.getPokemonByUrl(url)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            Log.e("API Error", e.message ?: "Unknown error")
            null
        }
    }

}