package com.example.gbot.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.gbot.model.ChatMessageWrapper
import com.example.gbot.model.Message
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addMessage(message: Message)

    @Update
    suspend fun updateMessage(message: Message)

    @Query("DELETE FROM Messages")
    suspend fun deleteMessages()

    @Query("SELECT * FROM Messages")
    fun getAllMessages() : Flow<List<Message>>

    @Query("SELECT * FROM Messages ORDER BY id DESC LIMIT 1")
    fun getLastMessage() : Flow<Message>

    @Query("SELECT role, content FROM Messages")
    fun getChatMessages() : Flow<List<ChatMessageWrapper>>
}