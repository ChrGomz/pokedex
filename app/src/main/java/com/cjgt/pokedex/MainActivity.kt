package com.cjgt.pokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.cjgt.pokedex.pantallas.router.Router
import com.cjgt.pokedex.ui.theme.PokedexTheme
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.perf.FirebasePerformance

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Firebase analytics
        Firebase.analytics.setAnalyticsCollectionEnabled(true)
        // Firebase Performance Monitoring
        FirebasePerformance.getInstance().isPerformanceCollectionEnabled = true
        // SharedPrefHandler
        val sharedPrefHandler = SharedPrefHandler(this)

        super.onCreate(savedInstanceState)
        setContent {
            PokedexTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Router(sharedPrefHandler)
                }
            }
        }
    }
}
