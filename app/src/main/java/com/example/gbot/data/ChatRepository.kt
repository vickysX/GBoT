package com.example.gbot.data

import com.example.gbot.model.ChatMessageWrapper
import com.example.gbot.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

    suspend fun deleteMessages()
    suspend fun addMessage(message : Message)
    suspend fun updateMessage(message: Message)
    fun getMessages() : Flow<List<Message>>
    fun getLastMessage() : Flow<Message>
    fun getChatMessages() : Flow<List<ChatMessageWrapper>>

}