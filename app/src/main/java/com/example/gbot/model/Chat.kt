package com.example.gbot.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Chat(
    @PrimaryKey
    @ColumnInfo(name = "chat_id")
    val chatId : Long,
    var title : String,
    val userId : String
)
