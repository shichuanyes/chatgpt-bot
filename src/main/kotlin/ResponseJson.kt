package com.github.shichuanyes.chatgpt

data class ResponseJson(
    val id: String,
    val created: Int,
    val usage: Usage,
    val choices: ArrayList<Choice>,
)

data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int,
)

data class Choice(
    val message: Message,
    val finish_reason: String,
    val index: Int
)