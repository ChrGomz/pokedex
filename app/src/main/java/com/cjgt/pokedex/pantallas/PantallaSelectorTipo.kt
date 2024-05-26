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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import java.util.Locale

@Composable
fun PantallaSelectorTipo(
    pokemonTypes: List<String>,
    imageLoader: ImageLoader,
    onTypeSelected: (String) -> Unit
) {

    pokemonTypes.chunked(3).let { chunkedList ->
        LazyColumn {
            itemsIndexed(chunkedList) { _, pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
                ) {
                    pair.forEach { type ->
                        val cardModifier = Modifier
                            .weight(1f)
                            .padding(8.dp)
                            .clickable { onTypeSelected(type.lowercase(Locale.ROOT)) }
                        TypeCard(type, imageLoader, cardModifier)
                    }
                }
            }
        }
    }
}

@Composable
fun TypeCard(type: String, imageLoader: ImageLoader, modifier: Modifier = Modifier) {
    val typeColor: Color = Color(TypeColors.getColorForType(type)).copy(alpha = 0.7f)
    val drawableId = TypeDrawable.getDrawableForType(type)
    Card(
        colors = CardDefaults.cardColors(containerColor = typeColor), modifier = modifier
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
                TypePhoto(100, drawableId, imageLoader)
                Text(
                    text = type,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun TypePhoto(
    size: Int, drawableID: Int, imageLoader: ImageLoader
) {
    val context = LocalContext.current

    val photoState = remember(drawableID) { mutableStateOf(false) }

    val imageRequest = remember(drawableID) {
        ImageRequest.Builder(context).data(drawableID).size(size).build()
    }

    imageLoader.enqueue(imageRequest)

    SubcomposeAsyncImage(
        model = imageRequest,
        loading = { CircularProgressIndicator() },
        error = {
            photoState.value = false
            Log.v("SpritePhoto", "Error loading image: ${imageRequest.data}")
        },
        contentDescription = null,
        contentScale = ContentScale.Crop,
        alignment = Alignment.Center,
        modifier = Modifier
            .clip(CircleShape)
            .size(size.dp)
    )
}