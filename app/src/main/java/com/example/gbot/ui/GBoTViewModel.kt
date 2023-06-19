package com.example.gbot.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.aallam.openai.api.BetaOpenAI
import com.example.gbot.GBoTApplication
import com.example.gbot.data.ChatRepository
import com.example.gbot.data.UserPreferencesRepository
import com.example.gbot.domain.GetMessageUseCase
import com.example.gbot.domain.SendAudioMessageUseCase
import com.example.gbot.domain.SendTextMessageUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GBoTViewModel(
    private val chatRepository: ChatRepository,
    //private val openAIRepository: OpenAIRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val sendAudioMessageUseCase: SendAudioMessageUseCase,
    private val sendTextMessageUseCase: SendTextMessageUseCase,
    private val getMessageUseCase: GetMessageUseCase
) : ViewModel() {

    //private val openAI = OpenAI(BuildConfig.OPEN_AI_API_KEY)
    val themeState : StateFlow<GBoTUIState.ThemeState> =
        userPreferencesRepository.isGiuliaTheme.map { isGiuliaLayout ->
            GBoTUIState.ThemeState(isGiuliaLayout)
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = GBoTUIState.ThemeState()
            )

    val chat : StateFlow<GBoTUIState.MessagesState> =
        chatRepository.getMessages().map { GBoTUIState.MessagesState(it)  }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = GBoTUIState.MessagesState()
            )
    /*@OptIn(BetaOpenAI::class)
    private val _chatMessages = mutableListOf<ChatMessage>()
    @OptIn(BetaOpenAI::class)
    val chatMessages : StateFlow<List<ChatMessage>> =
        chatRepository.getChatMessages().map { _chatMessages.add(it) }
            .stateIn(
                scope = viewModelScope,
                initialValue =
            )*/
    /*@OptIn(BetaOpenAI::class)
    private lateinit var chatMessages : List<ChatMessage>*/


    /*private var _messages  = mutableStateListOf(
        Message(
            textContent = "Ciao Giulia, chiedimi quello che vuoi!",
            isMine = false,
            messageState = MutableStateFlow(MessageState.Loading),
            timestamp = LocalTime.now(ZoneId.systemDefault()),
            isAudio = false
        )
    )
    val messages : List<Message>
        get() = _messages

    @OptIn(BetaOpenAI::class)
    private val oldChatMessages : MutableList<ChatMessage> = mutableListOf(
        ChatMessage(
            content = "Ciao Giulia, chiedimi quello che vuoi!",
            role = ChatRole.System
        )
    )*/
    var audioMode by mutableStateOf(true)
    var userInput by mutableStateOf("")
    //private var prompt = ""

    /*init {
        viewModelScope.launch {
            delay(1000L)
            _messages[0].messageState.update {
                MessageState.Success
            }
        }
    }*/

    fun toggleAppTheme(isGiuliaTheme : Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.saveThemePreference(isGiuliaTheme)
        }
    }

    fun deleteChat() {
        viewModelScope.launch {
            chatRepository.deleteMessages()
        }
    }

    fun switchMessageMode() {
        audioMode = userInput.isEmpty()
    }
    fun updateUserInput(input : String) {
        userInput = input
        audioMode = userInput.isEmpty()
    }

    @OptIn(BetaOpenAI::class)
    fun onTextMessageSent() {

        viewModelScope.launch {
            getMessageUseCase(sendTextMessageUseCase(userInput, false))
        }
        userInput = ""
        audioMode = userInput.isEmpty()
    }

    @OptIn(BetaOpenAI::class)
    fun onAudioMessageSent() {
        val fileName = ""
        viewModelScope.launch {
            getMessageUseCase(
                sendTextMessageUseCase(
                    sendAudioMessageUseCase(fileName), true
                )
            )
        }
    }

    /*@OptIn(BetaOpenAI::class)
    fun addMessageToChat() {
        viewModelScope.launch {
            chatRepository.addMessage(
                Message(
                    textContent = userInput,
                    isMine = true,
                    messageState = MutableStateFlow(MessageState.Success),
                    timestamp = LocalTime.now(ZoneId.systemDefault()),
                    isAudio = false,
                    chatMessage = ChatMessage(
                        content = userInput,
                        role = ChatRole.User,
                    )
                )
            )
        }
        userInput = ""
    }

    @OptIn(BetaOpenAI::class)
    fun sendNewMessage() : ChatCompletionRequest {
        getChatMessages()
        return ChatCompletionRequest(
            model = ModelId("gpt-4"),
            messages = chatMessages,
            temperature = 0.7,
            topP = 1.0
        )
    }

    @OptIn(BetaOpenAI::class)
    private fun getChatMessages() {
        viewModelScope.launch {
            chatMessages = chatRepository.getChatMessages().single()
        }
    }

    @OptIn(BetaOpenAI::class)
    fun appendMessageToChat() {
        _messages.add(
            Message(
                textContent = userInput,
                isMine = true,
                messageState = MutableStateFlow(MessageState.Success),
                timestamp = LocalTime.now(ZoneId.systemDefault()),
                isAudio = false
            )
        )
        oldChatMessages.add(
            ChatMessage(
                content = userInput,
                role = ChatRole.User,
            )
        )
        //prompt += "\n" + userInput
        userInput = ""
    }

    @OptIn(BetaOpenAI::class)
    private fun sendMessage() : ChatCompletionRequest {
        return ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = oldChatMessages as List<ChatMessage>,
            temperature = 0.7,
            topP = 1.0,
        )
    }

    @OptIn(BetaOpenAI::class)
    fun getResponse() {
        viewModelScope.launch {
            _messages.add(
                Message(
                    textContent = "",
                    isMine = false,
                    messageState = MutableStateFlow(MessageState.Loading),
                    timestamp = LocalTime.now(ZoneId.systemDefault()),
                    isAudio = false
                )
            )
            Log.d("MESSAGE STATUS", _messages.last().messageState.value.name)
            try {
                val completion = openAI.chatCompletion(sendMessage())
                completion.choices.map {choice ->
                    Log.d("VIEW MODEL", choice.message!!.content)
                    _messages.last().textContent += "\n" + choice.message!!.content
                    //prompt += _messages.last().body
                    _messages.last().textContent = _messages.last().textContent.trim()
                    _messages.last().timestamp = LocalTime
                        .now(ZoneId.systemDefault())
                    _messages.last().messageState.update {
                        MessageState.Success
                    }
                    oldChatMessages.add(
                        choice.message!!
                    )
                }
                Log.d("MESSAGE STATUS", _messages.last().messageState.value.name)
                _messages.forEach {
                    Log.d("MESSAGES", it.textContent)
                }
            } catch (exception : Exception) {
                exception.message?.let { Log.e("EXCEPTION", it) }
                _messages.last().messageState.update {
                    MessageState.Error
                }
                Log.d("MESSAGE STATUS", _messages.last().messageState.value.name)
            }
        }
    }
*/
    companion object {
        val factory = viewModelFactory {
            initializer {
                val appContainer = (this[APPLICATION_KEY] as GBoTApplication).appContainer
                val chatRepository = appContainer.chatRepository
                val userPreferencesRepository = appContainer.userPreferencesRepository
                val sendTextMessageUseCase = appContainer.sendTextMessageUseCase
                val sendAudioMessageUseCase = appContainer.sendAudioMessageUseCase
                val getMessageUseCase = appContainer.getMessageUseCase
                GBoTViewModel(
                    chatRepository = chatRepository,
                    userPreferencesRepository = userPreferencesRepository,
                    sendTextMessageUseCase = sendTextMessageUseCase,
                    sendAudioMessageUseCase = sendAudioMessageUseCase,
                    getMessageUseCase = getMessageUseCase
                )
            }
        }
    }

}

