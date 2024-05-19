package com.cjgt.pokedex.pantallas.testing

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
import androidx.navigation.NavController
import coil.ImageLoader
import com.cjgt.pokedex.pantallas.SpritePhoto
import com.cjgt.pokedex.pantallas.buildImageLoader
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
fun PantallaTesting(navController: NavController) {
    val context = LocalContext.current
    val database = LocalPokemonDB.getDatabase(context)
    val pokemonDao = database.pokemonDao()
    val pokemonRepository = PokemonRepository(pokemonDao)

}
