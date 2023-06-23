package com.example.gbot.model.module

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.aallam.openai.client.OpenAI
import com.example.gbot.BuildConfig.OPEN_AI_API_KEY
import com.example.gbot.data.GBoTDatabase
import com.example.gbot.data.MessageDao
import com.example.gbot.data.UserPreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

val Context.dataStore : DataStore<Preferences> by preferencesDataStore(
    name = "com.example.gbot.user_preferences"
)

@Module
@InstallIn(SingletonComponent::class)
object OpenAIAndDbModule {
    @Provides
    fun openAIClient() : OpenAI = OpenAI(OPEN_AI_API_KEY)

    @Provides
    fun database(@ApplicationContext context : Context) : MessageDao =
        GBoTDatabase.getDatabase(context).messageDao()

    @Provides
    fun dataStore(@ApplicationContext context : Context) : DataStore<Preferences> =
        context.dataStore

    @Provides
    fun userPreferences(dataStore: DataStore<Preferences>) : UserPreferencesRepository =
        UserPreferencesRepository(dataStore)
}