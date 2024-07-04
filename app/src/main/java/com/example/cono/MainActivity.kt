package com.example.cono

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.cono.ui.theme.ConoTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        var keepSplashScreenOn = true
        splashScreen.setKeepOnScreenCondition { keepSplashScreenOn }

        lifecycleScope.launch {
            delay(2000) // 2 seconds delay
            keepSplashScreenOn = false
        }

        val authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        setContent {
            ConoTheme {
                MyAppNavigation(authViewModel = authViewModel)
            }
        }
    }
}