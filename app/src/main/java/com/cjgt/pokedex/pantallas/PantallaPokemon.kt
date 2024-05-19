package com.cjgt.pokedex.pantallas

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import com.cjgt.pokedex.retrofit.Pokemon
import com.cjgt.pokedex.retrofit.TypeColors
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PantallaPokemon(pokemon: Pokemon, imageLoader: ImageLoader) {
    val pokemonColor: Color

    pokemon.types[0].type.name.let {
        pokemonColor = Color(TypeColors.getColorForType(it)).copy(alpha = 0.7f)
    }

    Box(contentAlignment = Alignment.CenterEnd, modifier = Modifier.fillMaxWidth()) {
        Icon(
            imageVector = Icons.Outlined.Star, contentDescription = "Star", modifier = Modifier
                .clip(CircleShape)
                .clickable {  }
                .padding(24.dp)
        )
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.background(pokemonColor)
        ) {
            SpritePhoto(
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
                text = pokemonName,
                style = TextStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                modifier = textModifier
            )
        }

        val tabs = listOf("Estadísticas", "Información")
        val pagerState = rememberPagerState(pageCount = { tabs.size })
        val scope = rememberCoroutineScope()

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
                }: ${stat.baseStat}", modifier = Modifier.weight(1f))
                CustomLinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp)
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
            .background(backgroundColor)
            .height(12.dp)
    ) {
        Box(
            modifier = Modifier
                .background(progressColor)
                .fillMaxHeight()
                .fillMaxWidth(progress)
        )
    }
}
//https://medium.com/@eozsahin1993/custom-progress-bars-in-jetpack-compose-723afb60c81c

@Composable
fun InformationTab(pokemon: Pokemon) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Text("Información adicional sobre ${pokemon.name}")
    }
}