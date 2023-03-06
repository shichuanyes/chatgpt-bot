package com.github.shichuanyes.chatgpt

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.result.Result
import com.google.gson.Gson

object RequestHandler {
    private val gson = Gson()

    @Throws(FuelError::class)
    fun getApiResponse(prompt: String, apiKey: String, systemMessage: String): String {
        if (apiKey.isBlank()) return "Please set Open AI API Key by\ncg setApiKey <api-key>"

        val sysMsg = Message(role = "system", content = systemMessage)
        val usrMsg = Message(role = "user", content = prompt)
        val msgArray: ArrayList<Message> = arrayListOf(sysMsg, usrMsg)
        msgArray.removeIf { msg: Message -> msg.content.isBlank() }
        val body = RequestJson(messages = msgArray)

        val (_, response, result) = Fuel.post("https://api.openai.com/v1/chat/completions")
            .authentication()
            .bearer(apiKey)
            .jsonBody(gson.toJson(body))
            .responseString()
        if (result is Result.Failure) throw result.getException()
        val json = gson.fromJson(response.data.decodeToString(), ResponseJson::class.java)
        return json.choices.first().message.content.trim()
    }
}