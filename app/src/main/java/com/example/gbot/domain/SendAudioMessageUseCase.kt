package com.example.gbot.domain

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.audio.TranscriptionRequest
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.model.ModelId
import com.example.gbot.data.ChatRepository
import com.example.gbot.data.OpenAIRepository
import com.example.gbot.model.ChatMessageWrapper
import com.example.gbot.model.Message
import com.example.gbot.ui.MessageState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path.Companion.toPath
import java.time.LocalTime
import java.time.ZoneId

class SendAudioMessageUseCase(
    private val openAIRepository: OpenAIRepository,
    private val chatRepository: ChatRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    @OptIn(BetaOpenAI::class)
    suspend operator fun invoke(fileName : String) : String =
        withContext(defaultDispatcher) {
            val message = Message(
                textContent = "",
                isMine = true,
                messageState = MutableStateFlow(MessageState.Success),
                timestamp = LocalTime.now(ZoneId.systemDefault()),
                isAudio = true,
                audioFilePath = fileName,
                chatMessage = ChatMessageWrapper(
                    content = "",
                    role = "user"
                )
            )
            chatRepository.addMessage(message)
            val request = TranscriptionRequest(
                audio = FileSource(fileName.toPath(), FileSystem.SYSTEM),
                model = ModelId("whisper-1"),
                responseFormat = "text"
            )
            val transcription = openAIRepository.getTranscriptionResponse(request)
            transcription.text
        }
}