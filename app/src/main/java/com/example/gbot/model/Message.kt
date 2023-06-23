package com.example.gbot.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.gbot.ui.MessageState
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalTime

@Entity(tableName = "Messages")
data class Message(

    @PrimaryKey(autoGenerate = true)
    var id : Long = 0,

    @ColumnInfo(name = "text_content")
    var textContent: String,

    @ColumnInfo(name = "is_mine")
    var isMine: Boolean,

    @ColumnInfo(name = "message_state")
    var messageState: MutableStateFlow<MessageState>,

    var timestamp: LocalTime? = null,

    var isAudio : Boolean,

    @ColumnInfo(name = "audio_file_path")
    var audioFilePath : String? = null,

    @Embedded var chatMessage: ChatMessageWrapper? = null,

    var chatConversationId : Long = 0
)


