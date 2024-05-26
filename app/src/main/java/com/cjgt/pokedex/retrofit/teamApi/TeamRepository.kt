package com.cjgt.pokedex.retrofit.teamApi

import android.util.Log
import retrofit2.Response

class TeamRepository {
    private val teamApiService: TeamApiService = RetrofitClient.retrofitInstance!!.create(
        TeamApiService::class.java
    )

    suspend fun createTeam(pokemonIds: List<Int>): Response<Team> {
        return try {
            teamApiService.createTeam(pokemonIds)
        } catch (e: Exception) {
            Log.e("API Error", e.message ?: "Unknown error")
            throw e
        }
    }

    suspend fun createTeam(id: Long, pokemonIds: List<Int>): Response<Team> {
        return try {
            teamApiService.createTeam(id, pokemonIds)
        } catch (e: Exception) {
            Log.e("API Error", e.message ?: "Unknown error")
            throw e
        }
    }

    suspend fun getTeam(id: Long): Response<Team> {
        return try {
            teamApiService.getTeam(id)
        } catch (e: Exception) {
            Log.e("API Error", e.message ?: "Unknown error")
            throw e
        }
    }

    suspend fun getAllTeams(): Response<List<Team>> {
        return try {
            teamApiService.getAllTeams()
        } catch (e: Exception) {
            Log.e("API Error", e.message ?: "Unknown error")
            throw e
        }
    }

    suspend fun updateTeam(id: Long, pokemonIds: List<Int>): Response<Team> {
        return try {
            teamApiService.updateTeam(id, pokemonIds)
        } catch (e: Exception) {
            Log.e("API Error", e.message ?: "Unknown error")
            throw e
        }
    }

    suspend fun deleteTeam(id: Long): Response<Void> {
        return try {
            teamApiService.deleteTeam(id)
        } catch (e: Exception) {
            Log.e("API Error", e.message ?: "Unknown error")
            throw e
        }
    }
}