package com.example.kontenin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.kontenin.ui.KonteninApp
import com.example.kontenin.ui.theme.KonteninTheme

class MainActivity : ComponentActivity() {

    // FE-07: Make appContainer internal so KonteninNavHost can access it
    internal lateinit var appContainer: AppContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appContainer = AppContainer()

        enableEdgeToEdge()
        setContent {
            KonteninTheme {
                KonteninApp(appContainer)
            }
        }
    }
}
