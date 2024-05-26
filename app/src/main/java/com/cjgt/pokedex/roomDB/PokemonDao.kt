package com.cjgt.pokedex.roomDB

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cjgt.pokedex.retrofit.pokeApi.Pokemon

@Dao
interface PokemonDao {
    @Query("SELECT * FROM Pokemon WHERE id = :id")
    suspend fun getPokemonById(id: Int): Pokemon?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemon(pokemon: Pokemon)
}