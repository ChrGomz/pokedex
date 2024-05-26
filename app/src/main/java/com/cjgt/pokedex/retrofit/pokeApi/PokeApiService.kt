package com.cjgt.pokedex.retrofit.pokeApi

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url


interface PokemonApiService {
    @GET("pokemon/{id}/")
    suspend fun getPokemonById(@Path("id") id: Int): Response<Pokemon>

    @GET("pokemon/{name}/")
    suspend fun getPokemonByName(@Path("name") name: String): Response<Pokemon>

    @GET("pokemon/")
    suspend fun getAllPokemon(@Query("limit") limit: Int, @Query("sort") sort: String): Response<PokemonListResponse>

    @GET
    suspend fun getPokemonByUrl(@Url url: String): Response<Pokemon>

    @GET("type/{type}")
    suspend fun getAllPokemonByType(@Path("type") type: String): Response<TypeResponse>
}