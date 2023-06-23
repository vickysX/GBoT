package com.example.gbot.model.module

import com.example.gbot.domain.GetMessage
import com.example.gbot.domain.GetMessageUseCase
import com.example.gbot.domain.SendTextMessage
import com.example.gbot.domain.SendTextMessageUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(ViewModelComponent::class)
abstract class UseCasesModule {

    @Binds
    abstract fun provideSendTextMessageUseCase(
        sendTextMessageUseCase: SendTextMessageUseCase
    ) : SendTextMessage

    @Binds
    abstract fun provideGetMessageUseCase(
        getMessageUseCase: GetMessageUseCase
    ) : GetMessage

    companion object {
        @Provides
        fun provideDispatcher() : CoroutineDispatcher = Dispatchers.Default
    }
}