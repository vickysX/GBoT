package com.example.gbot.ui

import android.system.Os.getenv
import androidx.lifecycle.ViewModel
import com.aallam.openai.client.OpenAI

class GbotViewModel : ViewModel() {
    private val openAI = OpenAI(getenv("OPEN_AI_API_KEY"))
}