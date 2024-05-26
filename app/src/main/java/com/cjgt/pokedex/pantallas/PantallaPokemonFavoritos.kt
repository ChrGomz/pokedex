package com.cjgt.pokedex.pantallas

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import com.cjgt.pokedex.R
import com.cjgt.pokedex.retrofit.pokeApi.Pokemon
import com.cjgt.pokedex.retrofit.pokeApi.PokemonRepository
import com.cjgt.pokedex.retrofit.teamApi.TeamRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun PantallaPokemonFav(teamRepository: TeamRepository, pokemonRepository: PokemonRepository, imageLoader: ImageLoader, onPokemonSelected: (Pokemon) -> Unit) {
    val userUID = stringToLong(Firebase.auth.currentUser?.uid ?: "")
    var favoritePokemonList by remember { mutableStateOf<List<Pokemon>?>(null) }

    LaunchedEffect(key1 = userUID) {
        val team = teamRepository.getTeam(userUID).body()
        favoritePokemonList = team?.pokemonIds?.mapNotNull { pokemonRepository.getPokemonById(it) }
    }

    LazyColumn {
        items(favoritePokemonList ?: emptyList()) { pokemon ->
            CardPokemonFav(teamRepository, pokemon, imageLoader, Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .clickable { onPokemonSelected(pokemon) })
        }
    }
}

@Composable
fun CardPokemonFav(teamRepository: TeamRepository, pokemon: Pokemon, imageLoader: ImageLoader, modifier: Modifier = Modifier) {
    val pokemonColor: Color
    val scope = rememberCoroutineScope()

    pokemon.types[0].type.name.let {
        pokemonColor = Color(TypeColors.getColorForType(it)).copy(alpha = 0.7f)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = pokemonColor),
        modifier = modifier
    ) {
        Row {
            SpritePhoto(size = 100, pokemon = pokemon, imageLoader = imageLoader)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color.Transparent)
            ) {
                Text(
                    text = pokemon.name,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = pokemon.types.joinToString { it.type.name },
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            val isInTeam = remember { mutableStateOf(false) }

            LaunchedEffect(pokemon) {
                scope.launch(Dispatchers.IO) { isInTeam.value = isPokemonInTeam(pokemon.id, teamRepository) }
            }
            Box(contentAlignment = Alignment.CenterEnd, modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
            ) {
                Image(painter = painterResource(id = if (isInTeam.value) R.drawable.filled_heart else R.drawable.outlined_heart),
                    contentDescription = if (isInTeam.value) "Remove from team" else "Add to team",
                    contentScale = ContentScale.FillBounds,
                    alpha = 0.4f,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(32.dp)
                        .clickable {
                            scope.launch(Dispatchers.IO) {
                                if (isInTeam.value) {
                                    deletePokemonFromTeam(pokemon.id, teamRepository)
                                } else {
                                    updateTeamList(pokemon.id, teamRepository)
                                }
                                isInTeam.value = !isInTeam.value
                            }
                        }
                )
            }
        }
    }
}
