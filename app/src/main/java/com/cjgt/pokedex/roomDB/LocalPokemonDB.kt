package com.cjgt.pokedex.roomDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cjgt.pokedex.retrofit.Pokemon

@Database(
    entities = [Pokemon::class], version = 2, exportSchema = false
)
@TypeConverters(Converters::class)
abstract class LocalPokemonDB : RoomDatabase() {
    abstract fun pokemonDao(): PokemonDao

    companion object {
        @Volatile
        private var INSTANCE: LocalPokemonDB? = null

        fun getDatabase(context: Context): LocalPokemonDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, LocalPokemonDB::class.java, "pokemon_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}