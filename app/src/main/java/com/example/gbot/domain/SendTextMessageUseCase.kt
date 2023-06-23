package com.example.gbot.domain

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.model.ModelId
import com.example.gbot.data.ChatRepository
import com.example.gbot.model.ChatMessageWrapper
import com.example.gbot.model.Message
import com.example.gbot.model.unwrap
import com.example.gbot.ui.MessageState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

interface SendTextMessage {
    @OptIn(BetaOpenAI::class)
    suspend operator fun invoke(userInput: String, isAudio: Boolean) : ChatCompletionRequest
}
class SendTextMessageUseCase @Inject constructor(
    /*private*/ val chatRepository: ChatRepository,
    /*private*/ val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) :  SendTextMessage {

    @OptIn(BetaOpenAI::class)
    override suspend operator fun invoke(userInput: String, isAudio : Boolean): ChatCompletionRequest =
        withContext(defaultDispatcher) {
            if (!isAudio) {
                val message = Message(
                    textContent = userInput,
                    isMine = true,
                    messageState = MutableStateFlow(MessageState.Success),
                    timestamp = LocalTime.now(ZoneId.systemDefault()),
                    isAudio = false,
                    chatMessage = ChatMessageWrapper(
                        content = userInput,
                        role = "user",
                    )
                )
                /*message.messageState.update {
                    MessageState.Success
                }*/
                chatRepository.addMessage(
                    message = message
                )
            } else {
                val lastMessage = chatRepository.getLastMessage().first()
                lastMessage.textContent = userInput
                lastMessage.chatMessage!!.content = userInput
                chatRepository.updateMessage(lastMessage)
            }

            val chatMessages = mutableListOf<ChatMessage>()
            chatRepository.getChatMessages().first().map {
                chatMessages.add(it.unwrap())
            }

            ChatCompletionRequest(
                model = ModelId("gpt-3.5-turbo"),
                messages = chatMessages as List<ChatMessage>,
                temperature = 0.7,
                topP = 1.0
            )
        }

}