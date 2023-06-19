package com.example.gbot.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.aallam.openai.client.OpenAI
import com.example.gbot.BuildConfig
import com.example.gbot.domain.GetMessageUseCase
import com.example.gbot.domain.SendAudioMessageUseCase
import com.example.gbot.domain.SendTextMessageUseCase

private const val OPEN_AI_API_KEY = BuildConfig.OPEN_AI_API_KEY
private const val THEME_PREFERENCE_NAME = "theme_preferences"
private val Context.dataStore : DataStore<Preferences> by preferencesDataStore(
    name = THEME_PREFERENCE_NAME
)

interface AppContainer {
    val chatRepository : ChatRepository
    //val openAIRepository : OpenAIRepository
    val userPreferencesRepository : UserPreferencesRepository
    val sendTextMessageUseCase : SendTextMessageUseCase
    val sendAudioMessageUseCase : SendAudioMessageUseCase
    val getMessageUseCase : GetMessageUseCase
}

class GBoTAppContainer(private val context: Context) : AppContainer {

    private val openAI = OpenAI(OPEN_AI_API_KEY)

    // initialize repositories
    override val chatRepository: ChatRepository by lazy {
        LocalChatRepository(GBoTDatabase.getDatabase(context).messageDao())
    }

    private val openAIRepository: OpenAIRepository by lazy {
        OpenAINetworkRepository(openAI)
    }

    override val userPreferencesRepository = UserPreferencesRepository(context.dataStore)

    override val sendTextMessageUseCase : SendTextMessageUseCase = SendTextMessageUseCase(chatRepository)
    override val getMessageUseCase : GetMessageUseCase =
        GetMessageUseCase(chatRepository, openAIRepository)
    override val sendAudioMessageUseCase : SendAudioMessageUseCase =
        SendAudioMessageUseCase(openAIRepository, chatRepository)
}