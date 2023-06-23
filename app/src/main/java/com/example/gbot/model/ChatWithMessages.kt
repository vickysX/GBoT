package com.example.gbot.model

import androidx.room.Embedded
import androidx.room.Relation

data class ChatWithMessages(
    @Embedded val chat : Chat,
    @Relation(
        parentColumn = "chatId",
        entityColumn = "chatConverationId"
    )
    val messages : List<Message>
)
