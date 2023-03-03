package com.github.shichuanyes.chatgpt

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
object Chatgpt : CompositeCommand(
    PluginMain, primaryName = "cg"
) {
    @SubCommand("setApiKey")
    suspend fun CommandSender.setApiKey(apiKey: String) {
        PluginConfig.apiKey = apiKey
        sendMessage("Open AI API Key set.")
    }
}