package com.example.myappgitmanager.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.myappgitmanager.ui.theme.MyAppGitManagerTheme
import com.example.myappgitmanager.navigation.AppNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAppGitManagerTheme {
                AppNavHost()
            }
        }
    }
}
