package com.example.gbot.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ToggleOff
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.gbot.model.Message
import com.example.gbot.R


sealed interface GBoTUIState {

    data class MessagesState(
        val messages: List<Message> = listOf()
    )

    data class ThemeState(
        val isGiuliaTheme: Boolean = true,
        val toggleIcon: ImageVector =
            if (isGiuliaTheme) {
                Icons.Default.ToggleOn
            } else {
                Icons.Default.ToggleOff
            },
        val toggleIconDescription : Int =
            if (isGiuliaTheme) {
                R.string.toggle_marsela
            } else {
                R.string.toggle_giulia
            }
    )

}
