package com.example.gbot.model

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole

data class ChatMessageWrapper(
    var role : String,
    var content : String
)

@OptIn(BetaOpenAI::class)
fun ChatMessageWrapper.unwrap() : ChatMessage = ChatMessage(
    role = ChatRole(this.role),
    content = this.content
)

/*@OptIn(BetaOpenAI::class)
fun ChatMessage.wrap() : ChatMessageWrapper = ChatMessageWrapper(
    role = this.role.role,
    content = this.content
)*/
