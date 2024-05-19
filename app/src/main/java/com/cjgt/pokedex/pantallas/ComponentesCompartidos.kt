package com.cjgt.pokedex.pantallas

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.SubcomposeAsyncImage
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.ImageRequest
import com.cjgt.pokedex.retrofit.Pokemon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


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

    if (photoState.value) {
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
    } else {
        CircularProgressIndicator(modifier = Modifier.size(size.dp))
        LaunchedEffect(key1 = pokemon.sprites.frontDefault) {
            scope.launch(Dispatchers.IO) {
                var delayTime = 1000L
                var attempts = 0
                while (!photoState.value && delayTime <= 10000L && attempts < 10) {
                    if (!pokemon.sprites.frontDefault?.contains("https")!!) {
                        imageRequest.newBuilder().data(pokemon.sprites.frontDefault).size(size).build()
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
