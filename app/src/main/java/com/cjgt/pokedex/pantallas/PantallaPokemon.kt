package com.cjgt.pokedex.pantallas

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import com.cjgt.pokedex.R
import com.cjgt.pokedex.retrofit.pokeApi.Pokemon
import com.cjgt.pokedex.retrofit.teamApi.TeamRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PantallaPokemon(pokemon: Pokemon, imageLoader: ImageLoader, teamRepository: TeamRepository) {
    val pokemonColor: Color
    val scope = rememberCoroutineScope()
    var isInTeam by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(pokemon) {
        scope.launch(Dispatchers.IO) {
            isInTeam = isPokemonInTeam(pokemon.id, teamRepository)
        }
    }

    pokemon.types[0].type.name.let {
        pokemonColor = Color(TypeColors.getColorForType(it)).copy(alpha = 0.7f)
    }

    Box(
        contentAlignment = Alignment.CenterEnd, modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Image(painter = painterResource(id = if (isInTeam) R.drawable.filled_heart else R.drawable.outlined_heart),
            contentDescription = if (isInTeam) "Remove from team" else "Add to team",
            contentScale = androidx.compose.ui.layout.ContentScale.FillBounds,
            modifier = Modifier
                .clip(CircleShape)
                .size(32.dp)
                .clickable {
                    scope.launch(Dispatchers.IO) {
                        if (isInTeam) {
                            deletePokemonFromTeam(pokemon.id, teamRepository)
                        } else {
                            updateTeamList(pokemon.id, teamRepository)
                        }
                        isInTeam = !isInTeam
                    }
                })
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.background(pokemonColor)
        ) {
            SpritePhotoWithZoom(
                size = 200, pokemon = pokemon, imageLoader = imageLoader
            )
            val pokemonName = pokemon.name.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
            }

            val textModifier = Modifier
                .fillMaxWidth()
                .background(pokemonColor)
                .padding(16.dp)

            Text(
                text = pokemonName, style = TextStyle(
                    fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White
                ), modifier = textModifier
            )
        }

        val tabs = listOf("Stats", "Info")
        val pagerState = rememberPagerState(pageCount = { tabs.size })

        TabRow(selectedTabIndex = pagerState.currentPage) {
            tabs.forEachIndexed { index, title ->
                Tab(selected = pagerState.currentPage == index, onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }, text = { Text(title) }, modifier = Modifier.height(48.dp)
                )
            }
        }

        HorizontalPager(state = pagerState) { page ->
            when (page) {
                0 -> StatisticsTab(pokemon)
                1 -> InformationTab(pokemon)
            }
        }
    }
}

@Composable
fun StatisticsTab(pokemon: Pokemon) {
    val textStyle = TextStyle(fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
    val textColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        pokemon.stats.forEach { stat ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("${
                    stat.statInfo.name.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.ROOT
                        ) else it.toString()
                    }
                }: ${stat.baseStat}", style = textStyle.copy(color = textColor), modifier = Modifier.weight(1f))
                CustomLinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                        .weight(1f),
                    progress = stat.baseStat / 255f
                )
            }
        }
    }
}

@Composable
fun CustomLinearProgressIndicator(
    modifier: Modifier = Modifier,
    progress: Float,
    progressColor: Color = Color.Green,
    backgroundColor: Color = Color.Blue,
    clipShape: Shape = RoundedCornerShape(16.dp)
) {
    Box(
        modifier = modifier
            .clip(clipShape)
            .background(backgroundColor.copy(alpha = 0.7f))
            .height(12.dp)
    ) {
        Box(
            modifier = Modifier
                .background(progressColor.copy(alpha = 0.7f))
                .fillMaxHeight()
                .fillMaxWidth(progress)
        )
    }
}
//https://medium.com/@eozsahin1993/custom-progress-bars-in-jetpack-compose-723afb60c81c

@Composable
fun InformationTab(pokemon: Pokemon) {
    val textStyle = TextStyle(fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
    val textColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
    val textModifier = Modifier
        .fillMaxWidth()
        .padding(4.dp)

    Column(
        horizontalAlignment = Alignment.Start, modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Text(AnnotatedString("ID: ${pokemon.id}"), style = textStyle, color = textColor, modifier = textModifier)
        Text(
            AnnotatedString("Types : ${pokemon.types.joinToString { it.type.name }}"),
            style = textStyle,
            color = textColor,
            modifier = textModifier
        )
        Text(
            AnnotatedString("Height: ${pokemon.height / 10.0}m"),
            style = textStyle,
            color = textColor,
            modifier = textModifier
        )
        Text(
            AnnotatedString("Weight: ${pokemon.weight / 10.0}kg"),
            style = textStyle,
            color = textColor,
            modifier = textModifier
        )

    }
}

suspend fun isPokemonInTeam(pokemonId: Int, teamRepository: TeamRepository): Boolean {
    stringToLong(Firebase.auth.currentUser?.uid).let { userId ->
        val response = teamRepository.getTeam(userId)

        if (response.isSuccessful) {
            val team = response.body()
            team?.let {
                return it.pokemonIds.contains(pokemonId)
            }
        } else if (response.code() == 404) {
            val createResponse = teamRepository.createTeam(userId, listOf(pokemonId))
            if (createResponse.isSuccessful) {
                Log.d("TeamApi", "New team created with Pokemon id $pokemonId")
                return true
            } else {
                Log.e(
                    "TeamApi",
                    "Failed to create new team: ${createResponse.errorBody()?.string()}"
                )
            }
        } else {
            Log.e("TeamApi", "Failed to get team: ${response.errorBody()?.string()}")
        }
    }
    return false
}

suspend fun updateTeamList(pokemonId: Int, teamRepository: TeamRepository) {
    stringToLong(Firebase.auth.currentUser?.uid).let { userId ->
        val response = teamRepository.getTeam(userId)

        if (response.isSuccessful) {
            val team = response.body()
            team?.let {
                val updatedPokemonIds = it.pokemonIds.toMutableList()
                if (updatedPokemonIds.contains(pokemonId)) {
                    updatedPokemonIds.remove(pokemonId)
                    teamRepository.updateTeam(userId, updatedPokemonIds)
                    Log.d("TeamApi", "Pokemon with id $pokemonId removed from team with id $userId")
                } else {
                    updatedPokemonIds.add(pokemonId)
                    teamRepository.updateTeam(userId, updatedPokemonIds)
                    Log.d("TeamApi", "Pokemon with id $pokemonId added to team with id $userId")
                }
            }
        }
    }
}

suspend fun deletePokemonFromTeam(pokemonId: Int, teamRepository: TeamRepository) {
    stringToLong(Firebase.auth.currentUser?.uid).let { userId ->
        val response = teamRepository.getTeam(userId)

        if (response.isSuccessful) {
            val team = response.body()
            team?.let {
                val updatedPokemonIds = it.pokemonIds.toMutableList()
                if (updatedPokemonIds.contains(pokemonId)) {
                    updatedPokemonIds.remove(pokemonId)
                    teamRepository.updateTeam(userId, updatedPokemonIds)
                    Log.d("TeamApi", "Pokemon with id $pokemonId removed from team with id $userId")
                }
            }
        }
    }
}










