package com.cjgt.pokedex.pantallas.testing

import Pokemon
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.SubcomposeAsyncImage
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.ImageRequest
import com.cjgt.pokedex.R
import com.cjgt.pokedex.retrofit.PokemonListItem
import com.cjgt.pokedex.retrofit.PokemonRepository
import com.cjgt.pokedex.retrofit.TypeColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun PantallaTesting(navController: NavController) {
    val pokemonRepository = remember { PokemonRepository() }
    var pokemonList by remember { mutableStateOf<List<Pokemon>?>(null) }

    val context = LocalContext.current

    val imageLoader = ImageLoader.Builder(context).dispatcher(Dispatchers.IO).memoryCache {
        MemoryCache.Builder(context).maxSizePercent(0.10).build()
    }.diskCache {
        DiskCache.Builder().directory(context.cacheDir.resolve("image_cache")).maxSizePercent(0.02)
            .build()
    }.respectCacheHeaders(false).build()

    LaunchedEffect(key1 = true) {
        withContext(Dispatchers.IO) {
            val pokemonListData = fetchPokemonList(pokemonRepository)
            val fetchedPokemonList = fetchPokemonDetails(pokemonRepository, pokemonListData)
            pokemonList = fetchedPokemonList
        }
    }

    pokemonList?.sortedBy { it.id }?.chunked(2)?.let { chunkedList ->
        LazyColumn(
            modifier = Modifier.background(
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                shape = RectangleShape
            )
        ) {
            itemsIndexed(chunkedList) { _, pair ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    pair.forEach { pokemon ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterVertically)
                                .weight(1f)
                        ) {
                            PokemonCard(pokemon, imageLoader) {
                                // onClick action
                            }
                        }
                    }
                }
            }
        }
    }
}

private suspend fun fetchPokemonList(pokemonRepository: PokemonRepository): List<PokemonListItem>? {
    return try {
        pokemonRepository.getAllPokemon(50, "id")
    } catch (e: Exception) {
        Log.e("API Error", e.message ?: "Unknown error")
        null
    }
}

private suspend fun fetchPokemonDetails(
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
fun SpritePhoto(
    size: Int, pokemon: Pokemon, imageLoader: ImageLoader
) {
    val context = LocalContext.current

    val imageModel =
        ImageRequest.Builder(context).data(pokemon.sprites.frontDefault).size(size).build()

    imageLoader.enqueue(imageModel)

    SubcomposeAsyncImage(
        model = imageModel,
        loading = { CircularProgressIndicator() },
        error = {
            DefaultImage()
        },
        contentDescription = null,
        contentScale = ContentScale.Crop,
        alignment = Alignment.Center,
        modifier = Modifier
            .clip(CircleShape)
            .size(size.dp)
    )

}

@Composable
fun DefaultImage() {
    Image(
        painter = painterResource(id = R.drawable.ic_launcher_background), contentDescription = null
    )

}

@Composable
fun PokemonCard(pokemon: Pokemon, imageLoader: ImageLoader, onClick: () -> Unit) {
    val pokemonColor: Color

    pokemon.types[0].type.name.let {
        pokemonColor = Color(TypeColors.getColorForType(it)).copy(alpha = 0.7f)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = pokemonColor),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .background(Color.Transparent, shape = RectangleShape)
        ) {
            Column(
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

