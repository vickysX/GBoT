package com.example.gbot.model

import com.example.gbot.ui.MessageState
import kotlinx.coroutines.flow.MutableStateFlow

data class Message(
    var body : String,
    val isMine : Boolean,
    var messageState : MutableStateFlow<MessageState>
)


