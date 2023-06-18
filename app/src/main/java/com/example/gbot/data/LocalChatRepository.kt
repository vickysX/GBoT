package com.example.gbot.data

import com.example.gbot.model.ChatMessageWrapper
import com.example.gbot.model.Message
import kotlinx.coroutines.flow.Flow


class LocalChatRepository(private val messageDao: MessageDao) : ChatRepository {

    override suspend fun deleteMessages() = messageDao.deleteMessages()

    override fun getMessages() : Flow<List<Message>> = messageDao.getAllMessages()

    override suspend fun addMessage(message: Message) = messageDao.addMessage(message)

    override suspend fun updateMessage(message: Message) = messageDao.updateMessage(message)

    override fun getLastMessage() : Flow<Message> = messageDao.getLastMessage()

    override fun getChatMessages(): Flow<List<ChatMessageWrapper>> = messageDao.getChatMessages()

}