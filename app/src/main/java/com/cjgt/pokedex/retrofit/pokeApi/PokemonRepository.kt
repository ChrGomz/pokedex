package com.cjgt.pokedex.retrofit.pokeApi

import android.util.Log
import com.cjgt.pokedex.roomDB.PokemonDao
import com.cjgt.pokedex.retrofit.pokeApi.RetrofitClient.retrofitInstance


class PokemonRepository(private val pokemonDao: PokemonDao) {
    private val pokemonApiService: PokemonApiService = retrofitInstance!!.create(
        PokemonApiService::class.java
    )

    suspend fun getPokemonById(id: Int): Pokemon? {
        val localPokemon = pokemonDao.getPokemonById(id)
        if (localPokemon != null) {
            return localPokemon
        }
        return try {
            val response = pokemonApiService.getPokemonById(id)
            if (response.isSuccessful) {
                val pokemon = response.body()
                pokemon?.let { pokemonDao.insertPokemon(it) }
                pokemon
            } else null
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

    suspend fun getAllPokemonByType(type: String): List<PokemonListItem>? {
        return try {
            val response = pokemonApiService.getAllPokemonByType(type)
            val mappedResponse = response.body()?.pokemon?.map { it.pokemon } ?: emptyList()
            if (response.isSuccessful) mappedResponse else null
        } catch (e: Exception) {
            Log.e("API Error", e.message ?: "Unknown error")
            null
        }
    }

    suspend fun getPokemonByUrl(url: String): Pokemon? {
        val id = url.split("/").last { it.isNotEmpty() }.toInt()

        val localPokemon = pokemonDao.getPokemonById(id)
        if (localPokemon != null) {
            //Log.v("CHRIS_DEBUG", "pokemon $id fetched from roomDB")
            return localPokemon
        }
        //Log.v("CHRIS_DEBUG", "pokemon $id fetched from API")

        return try {
            val response = pokemonApiService.getPokemonByUrl(url)
            if (response.isSuccessful) {
                val pokemon = response.body()
                pokemon?.let { pokemonDao.insertPokemon(it) }
                pokemon
            } else null
        } catch (e: Exception) {
            Log.e("API Error", e.message ?: "Unknown error")
            null
        }
    }

}