package com.example.kontenin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.kontenin.ui.preview.ThemePreviewScreen
import com.example.kontenin.ui.theme.KonteninTheme

class MainActivity : ComponentActivity() {

    private lateinit var appContainer: AppContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appContainer = AppContainer()

        enableEdgeToEdge()
        setContent {
            KonteninTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ThemePreviewScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    KonteninTheme {
        ThemePreviewScreen()
    }
}
