package com.github.shichuanyes.chatgpt

data class RequestJson(
    val model: String = "gpt-3.5-turbo",
    val messages: MutableList<Message>
)
