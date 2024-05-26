package com.cjgt.pokedex.pantallas

import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.SubcomposeAsyncImage
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.ImageRequest
import com.cjgt.pokedex.R
import com.cjgt.pokedex.retrofit.pokeApi.Pokemon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale


fun buildImageLoader(context: Context): ImageLoader {
    return ImageLoader.Builder(context).dispatcher(Dispatchers.IO).memoryCache {
        MemoryCache.Builder(context).maxSizePercent(0.10).build()
    }.diskCache {
        DiskCache.Builder().directory(context.cacheDir.resolve("image_cache")).maxSizePercent(0.02)
            .build()
    }.respectCacheHeaders(false).build()
}


@Composable
fun SpritePhoto(
    size: Int, pokemon: Pokemon, imageLoader: ImageLoader
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    if (pokemon.sprites.frontDefault == null) {
        pokemon.sprites.frontDefault = ""
    }

    val photoState = remember(pokemon.sprites.frontDefault) { mutableStateOf(false) }

    val imageRequest = remember(pokemon.sprites.frontDefault) {
        ImageRequest.Builder(context).data(pokemon.sprites.frontDefault).size(size).build()
    }

    imageLoader.enqueue(imageRequest)

    if (photoState.value) {
        SubcomposeAsyncImage(model = imageRequest,
            loading = { CircularProgressIndicator() },
            error = {
                photoState.value = false
                Log.v("SpritePhoto", "Error loading image: ${imageRequest.data}")
            },
            contentDescription = null,
            contentScale = ContentScale.Fit,
            alignment = Alignment.Center,
            modifier = Modifier
                .clip(RectangleShape)
                .size(size.dp)
        )
    } else {
        CircularProgressIndicator(modifier = Modifier.size(size.dp))
        LaunchedEffect(key1 = pokemon.sprites.frontDefault) {
            scope.launch(Dispatchers.IO) {
                var delayTime = 1000L
                var attempts = 0
                while (!photoState.value && delayTime <= 10000L && attempts < 10) {
                    if (!pokemon.sprites.frontDefault?.contains("https")!!) {
                        imageRequest.newBuilder().data(pokemon.sprites.frontDefault).size(size)
                            .build()
                        imageLoader.enqueue(imageRequest)
                        if (pokemon.sprites.frontDefault?.contains("https") == true) {
                            Log.d("SpritePhoto", "PhotoURL: ${pokemon.sprites.frontDefault}")
                            photoState.value = true
                        } else {
                            delayTime += 1000L
                            delay(delayTime)
                            attempts++
                        }
                    } else {
                        photoState.value = true
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SpritePhotoWithZoom(
    size: Int, pokemon: Pokemon, imageLoader: ImageLoader
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    if (pokemon.sprites.frontDefault == null) {
        pokemon.sprites.frontDefault = ""
    }

    val photoState = remember(pokemon.sprites.frontDefault) { mutableStateOf(false) }

    val imageRequest = remember(pokemon.sprites.frontDefault) {
        ImageRequest.Builder(context).data(pokemon.sprites.frontDefault).size(size).build()
    }

    imageLoader.enqueue(imageRequest)

    if (photoState.value) {
        var scale by remember { mutableFloatStateOf(1f) }
        var offset by remember { mutableStateOf(Offset(0f, 0f)) }
        val haptics = LocalHapticFeedback.current

        SubcomposeAsyncImage(model = imageRequest,
            loading = { CircularProgressIndicator() },
            error = {
                photoState.value = false
                Log.v("SpritePhoto", "Error loading image: ${imageRequest.data}")
            },
            contentDescription = null,
            contentScale = ContentScale.Fit,
            alignment = Alignment.Center,
            modifier = Modifier
                .clip(RectangleShape)
                .size(size.dp)
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale *= zoom
                        scale = scale.coerceIn(0.5f, 3f)
                        offset = if (scale == 1f) Offset(0f, 0f) else offset + pan
                    }
                }
                .graphicsLayer(
                    scaleX = scale, scaleY = scale, translationX = offset.x, translationY = offset.y
                )
                .combinedClickable(onClick = {}, onLongClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    scale = 1f
                    offset = Offset(0f, 0f)
                }, onDoubleClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    scale = 1f
                    offset = Offset(0f, 0f)
                })

        )


    } else {
        CircularProgressIndicator(modifier = Modifier.size(size.dp))
        LaunchedEffect(key1 = pokemon.sprites.frontDefault) {
            scope.launch(Dispatchers.IO) {
                var delayTime = 1000L
                var attempts = 0
                while (!photoState.value && delayTime <= 10000L && attempts < 10) {
                    if (!pokemon.sprites.frontDefault?.contains("https")!!) {
                        imageRequest.newBuilder().data(pokemon.sprites.frontDefault).size(size)
                            .build()
                        imageLoader.enqueue(imageRequest)
                        if (pokemon.sprites.frontDefault?.contains("https") == true) {
                            Log.d("SpritePhoto", "PhotoURL: ${pokemon.sprites.frontDefault}")
                            photoState.value = true
                        } else {
                            delayTime += 1000L
                            delay(delayTime)
                            attempts++
                        }
                    } else {
                        photoState.value = true
                    }
                }
            }
        }
    }
}

object TypeColors {
    private val typeColorMap = mapOf(
        "normal" to Color.parseColor("#A8A77A"),
        "fire" to Color.parseColor("#EE8130"),
        "fuego" to Color.parseColor("#EE8130"),
        "water" to Color.parseColor("#6390F0"),
        "agua" to Color.parseColor("#6390F0"),
        "electric" to Color.parseColor("#F7D02C"),
        "eléctrico" to Color.parseColor("#F7D02C"),
        "grass" to Color.parseColor("#7AC74C"),
        "tierra" to Color.parseColor("#7AC74C"),
        "ice" to Color.parseColor("#96D9D6"),
        "hielo" to Color.parseColor("#96D9D6"),
        "fighting" to Color.parseColor("#C22E28"),
        "lucha" to Color.parseColor("#C22E28"),
        "poison" to Color.parseColor("#A33EA1"),
        "veneno" to Color.parseColor("#A33EA1"),
        "ground" to Color.parseColor("#E2BF65"),
        "suelo" to Color.parseColor("#E2BF65"),
        "flying" to Color.parseColor("#A98FF3"),
        "volador" to Color.parseColor("#A98FF3"),
        "psychic" to Color.parseColor("#F95587"),
        "psíquico" to Color.parseColor("#F95587"),
        "bug" to Color.parseColor("#A6B91A"),
        "bicho" to Color.parseColor("#A6B91A"),
        "rock" to Color.parseColor("#B6A136"),
        "roca" to Color.parseColor("#B6A136"),
        "ghost" to Color.parseColor("#735797"),
        "fantasma" to Color.parseColor("#735797"),
        "dragon" to Color.parseColor("#6F35FC"),
        "dragón" to Color.parseColor("#6F35FC"),
        "dark" to Color.parseColor("#705746"),
        "siniestro" to Color.parseColor("#705746"),
        "steel" to Color.parseColor("#B7B7CE"),
        "acero" to Color.parseColor("#B7B7CE"),
        "fairy" to Color.parseColor("#D685AD"),
        "hada" to Color.parseColor("#D685AD")
    )

    fun getColorForType(type: String): Int {
        return typeColorMap[type.lowercase(Locale.ROOT)] ?: Color.parseColor("#000000")
    }
}

object TypeDrawable {
    private val typeDrawableMap = mapOf(
        "normal" to R.drawable.normal,
        "fire" to R.drawable.fire,
        "fuego" to R.drawable.fire,
        "water" to R.drawable.water,
        "agua" to R.drawable.water,
        "electric" to R.drawable.electric,
        "eléctrico" to R.drawable.electric,
        "grass" to R.drawable.grass,
        "tierra" to R.drawable.grass,
        "ice" to R.drawable.ice,
        "hielo" to R.drawable.ice,
        "fighting" to R.drawable.fighting,
        "lucha" to R.drawable.fighting,
        "poison" to R.drawable.poison,
        "veneno" to R.drawable.poison,
        "ground" to R.drawable.ground,
        "suelo" to R.drawable.ground,
        "flying" to R.drawable.flying,
        "volador" to R.drawable.flying,
        "psychic" to R.drawable.psychic,
        "psíquico" to R.drawable.psychic,
        "bug" to R.drawable.bug,
        "bicho" to R.drawable.bug,
        "rock" to R.drawable.rock,
        "roca" to R.drawable.rock,
        "ghost" to R.drawable.ghost,
        "fantasma" to R.drawable.ghost,
        "dragon" to R.drawable.dragon,
        "dragón" to R.drawable.dragon,
        "dark" to R.drawable.dark,
        "siniestro" to R.drawable.dark,
        "steel" to R.drawable.steel,
        "acero" to R.drawable.steel,
        "fairy" to R.drawable.fairy,
        "hada" to R.drawable.fairy
    )

    fun getDrawableForType(type: String): Int {
        return typeDrawableMap[type.lowercase(Locale.ROOT)] ?: R.drawable.normal
    }
}


fun stringToLong(s: String?): Long {
    val bytes = s?.toByteArray(Charsets.UTF_8)
    var result: Long = 0
    if (bytes != null) {
        for (i in bytes.indices) {
            result = result shl 8 or (bytes[i].toInt() and 0xff).toLong()
        }
    }
    return result
}

