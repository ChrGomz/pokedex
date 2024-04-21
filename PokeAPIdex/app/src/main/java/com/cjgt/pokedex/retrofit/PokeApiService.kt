package com.cjgt.pokedex.retrofit

import retrofit2.Call
import retrofit2.http.GET

interface PokeApiService {
    @GET("pokemon")
    fun getPokemon(): Call<PokemonResponse>
}