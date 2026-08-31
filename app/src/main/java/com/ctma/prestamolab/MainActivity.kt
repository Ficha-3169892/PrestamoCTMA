package com.ctma.prestamolab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.ctma.prestamolab.ui.PrestamoLabApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Color(0xFF165B4D),
                    secondary = Color(0xFF496A80),
                    tertiary = Color(0xFF7A5C20),
                    surface = Color(0xFFFFFBFE),
                )
            ) {
                PrestamoLabApp()
            }
        }
    }
}
