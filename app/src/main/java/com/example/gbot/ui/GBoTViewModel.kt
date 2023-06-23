package com.example.gbot.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
/**/import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.aallam.openai.api.BetaOpenAI
import com.example.gbot.GBoTApp
import com.example.gbot.data.ChatRepository
import com.example.gbot.data.UserPreferencesRepository
import com.example.gbot.domain.GetMessage
import com.example.gbot.domain.GetMessageUseCase
import com.example.gbot.domain.SendAudioMessageUseCase
import com.example.gbot.domain.SendTextMessage
import com.example.gbot.domain.SendTextMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GBoTViewModel @Inject constructor(
    /*private*/ val chatRepository: ChatRepository,
    //private val openAIRepository: OpenAIRepository,
    /*private*/ val userPreferencesRepository: UserPreferencesRepository,
    /*private*/ //val sendAudioMessageUseCase: SendAudioMessageUseCase,
    /*private*/ val sendTextMessageUseCase: SendTextMessage,
    /*private*/ val getMessageUseCase: GetMessage
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

    var audioMode by mutableStateOf(true)
    var userInput by mutableStateOf("")


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

    /*@OptIn(BetaOpenAI::class)
    fun onAudioMessageSent() {
        val fileName = ""
        viewModelScope.launch {
            getMessageUseCase(
                sendTextMessageUseCase(
                    sendAudioMessageUseCase(fileName), true
                )
            )
        }
    }*/


    /*companion object {
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
                    //sendAudioMessageUseCase = sendAudioMessageUseCase,
                    getMessageUseCase = getMessageUseCase
                )
            }
        }
    }*/

}

