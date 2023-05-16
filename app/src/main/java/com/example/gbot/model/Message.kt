package com.example.gbot.model

import com.example.gbot.ui.MessageState
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalTime

data class Message(
    var content: String,
    val isMine: Boolean,
    var timeStamp: LocalTime? = null,
    var messageState: MutableStateFlow<MessageState>,
    val isAudio : Boolean
)


