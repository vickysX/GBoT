package com.example.gbot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gbot.ui.ChatScreen
import com.example.gbot.ui.GBoTViewModel
import com.example.gbot.ui.theme.GBoTTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel : GBoTViewModel = hiltViewModel(/*factory = GBoTViewModel.factory*/)
            val themeState = viewModel.themeState.collectAsState()
            val isGiuliaTheme = themeState.value.isGiuliaTheme
            GBoTTheme(isGiuliaTheme = isGiuliaTheme) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChatScreen(viewModel = viewModel)
                }
            }
        }
    }
}