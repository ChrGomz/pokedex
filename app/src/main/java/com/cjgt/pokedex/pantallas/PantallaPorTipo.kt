package com.cjgt.pokedex.pantallas

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import com.cjgt.pokedex.retrofit.pokeApi.Pokemon
import com.cjgt.pokedex.retrofit.pokeApi.PokemonListItem
import com.cjgt.pokedex.retrofit.pokeApi.PokemonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun PantallaPorTipo(
    pokemonRepository: PokemonRepository,
    imageLoader: ImageLoader,
    type: String,
    onPokemonSelected: (Pokemon) -> Unit
) {
    var pokemonList by remember { mutableStateOf<List<Pokemon>?>(null) }

    LaunchedEffect(key1 = type) {
        withContext(Dispatchers.IO) {
            val pokemonListData = fetchPokemonListByType(pokemonRepository, type)
            val fetchedPokemonList = fetchPokemonDetails(pokemonRepository, pokemonListData)
            pokemonList = fetchedPokemonList
        }
    }

    pokemonList?.sortedBy { it.id }?.let { sortedList ->
        LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 140.dp)){
            items(sortedList) {  pokemon ->
                PokemonCard(pokemon, imageLoader, Modifier.padding(8.dp).fillMaxWidth().clickable {
                    onPokemonSelected(pokemon)
                })
            }
        }
    }
}

suspend fun fetchPokemonListByType(
    pokemonRepository: PokemonRepository,
    type: String
): List<PokemonListItem>? {
    return try {
        pokemonRepository.getAllPokemonByType(type)
    } catch (e: Exception) {
        Log.e("API Error", e.message ?: "Unknown error")
        null
    }
}