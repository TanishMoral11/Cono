package com.example.cono

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
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

        // Create a custom ViewModelProvider.Factory
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                    return AuthViewModel(this@MainActivity) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }

        val authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        setContent {
            ConoTheme {
                MyAppNavigation(authViewModel = authViewModel)
            }
        }
    }
}
