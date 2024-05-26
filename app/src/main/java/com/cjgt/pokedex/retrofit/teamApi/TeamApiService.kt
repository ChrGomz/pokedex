package com.cjgt.pokedex.retrofit.teamApi

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TeamApiService {
    @POST("teams")
    suspend fun createTeam(@Body pokemonIds: List<Int>): Response<Team>

    @POST("teams/{id}")
    suspend fun createTeam(@Path("id") id: Long, @Body pokemonIds: List<Int>): Response<Team>

    @GET("teams/{id}")
    suspend fun getTeam(@Path("id") id: Long): Response<Team>

    @GET("teams")
    suspend fun getAllTeams(): Response<List<Team>>

    @PUT("teams/{id}")
    suspend fun updateTeam(@Path("id") id: Long, @Body pokemonIds: List<Int>): Response<Team>

    @DELETE("teams/{id}")
    suspend fun deleteTeam(@Path("id") id: Long): Response<Void>
}