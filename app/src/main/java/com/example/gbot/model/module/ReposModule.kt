package com.example.gbot.model.module

import com.example.gbot.data.ChatRepository
import com.example.gbot.data.LocalChatRepository
import com.example.gbot.data.OpenAINetworkRepository
import com.example.gbot.data.OpenAIRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ReposModule {
    @Binds
    abstract fun provideChatRepository(
        chatRepository: LocalChatRepository
    ) : ChatRepository

    @Binds
    abstract fun provideOpenAIRepository(
        openAIRepository : OpenAINetworkRepository
    ) : OpenAIRepository

    /*@Binds
    abstract fun provideUserPreferences(
        //userPreferencesRepository: UserPreferencesRepository
    ) : UserPreferencesRepository*/
}