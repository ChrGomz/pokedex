package com.cjgt.pokedex.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import com.cjgt.pokedex.retrofit.pokeApi.Pokemon

@Composable
fun PantallaBuscador(
    pokemonList: List<Pokemon>?, imageLoader: ImageLoader, onPokemonSelected: (Pokemon) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    Column {
        TextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Buscador de Pokemon") },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search, contentDescription = "Search Icon"
                )
            }
        )

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        val filteredList = pokemonList?.filter { it.name.contains(searchText, ignoreCase = true) }

        LazyColumn {
            items(filteredList ?: listOf()) { pokemon ->
                var pokemonColor: Color
                pokemon.types[0].type.name.let {
                    pokemonColor = Color(TypeColors.getColorForType(it)).copy(alpha = 0.7f)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(pokemonColor)
                        .clickable { onPokemonSelected(pokemon) }
                ) {
                    Spacer(modifier = Modifier.padding(16.dp))
                    SpritePhoto(50, pokemon, imageLoader)
                    Column {
                        Text(text = pokemon.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp))

                    }
                }
            }
        }
    }
}