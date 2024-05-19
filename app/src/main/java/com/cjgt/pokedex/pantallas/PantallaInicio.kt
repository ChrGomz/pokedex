package com.cjgt.pokedex.pantallas

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import com.cjgt.pokedex.retrofit.Pokemon
import com.cjgt.pokedex.retrofit.PokemonListItem
import com.cjgt.pokedex.retrofit.PokemonRepository
import com.cjgt.pokedex.retrofit.TypeColors
import com.cjgt.pokedex.roomDB.LocalPokemonDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun PantallaInicio(pokemonRepository: PokemonRepository, imageLoader: ImageLoader, onPokemonSelected: (Pokemon) -> Unit) {
    var pokemonList by remember { mutableStateOf<List<Pokemon>?>(null) }

    LaunchedEffect(key1 = true) {
        withContext(Dispatchers.IO) {
            val pokemonListData = fetchPokemonList(pokemonRepository)
            val fetchedPokemonList = fetchPokemonDetails(pokemonRepository, pokemonListData)
            pokemonList = fetchedPokemonList
        }
    }

    pokemonList?.sortedBy { it.id }?.chunked(2)?.let { chunkedList ->
        LazyColumn {
            itemsIndexed(chunkedList) { _, pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
                ) {
                    pair.forEach { pokemon ->
                        val cardModifier = Modifier
                            .weight(1f)
                            .padding(8.dp)
                            .clickable { onPokemonSelected(pokemon) }
                        PokemonCard(pokemon, imageLoader, cardModifier)
                    }
                }
            }
        }
    }
}

suspend fun fetchPokemonList(pokemonRepository: PokemonRepository): List<PokemonListItem>? {
    return try {
        pokemonRepository.getAllPokemon(20, "id")
    } catch (e: Exception) {
        Log.e("API Error", e.message ?: "Unknown error")
        null
    }
}

suspend fun fetchPokemonDetails(
    pokemonRepository: PokemonRepository, pokemonListData: List<PokemonListItem>?
): List<Pokemon> {
    val fetchedPokemonList = mutableListOf<Pokemon>()
    coroutineScope {
        pokemonListData?.parallelStream()?.forEach { pokemonListItem ->
            launch(Dispatchers.IO) {
                try {
                    val pokemon = pokemonRepository.getPokemonByUrl(pokemonListItem.url)
                    if (pokemon != null) {
                        fetchedPokemonList.add(pokemon)
                    }
                } catch (e: Exception) {
                    Log.e("API Error", e.message ?: "Unknown error")
                }
            }
        }
    }
    return fetchedPokemonList
}

@Composable
fun PokemonCard(pokemon: Pokemon, imageLoader: ImageLoader, modifier: Modifier = Modifier) {
    val pokemonColor: Color

    pokemon.types[0].type.name.let {
        pokemonColor = Color(TypeColors.getColorForType(it)).copy(alpha = 0.7f)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = pokemonColor), modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .background(Color.Transparent, shape = RectangleShape)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                SpritePhoto(100, pokemon, imageLoader)
                Text(
                    text = pokemon.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = pokemon.types.joinToString { it.type.name },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}