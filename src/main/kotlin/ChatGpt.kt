@file:Suppress("unused")
package com.github.shichuanyes.chatgpt

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
object ChatGpt : CompositeCommand(
    PluginMain, primaryName = "cg"
) {
    @SubCommand("setApiKey")
    suspend fun CommandSender.setApiKey(apiKey: String) {
        PluginConfig.apiKey = apiKey
        sendMessage("Open AI API Key set")
    }

    @SubCommand("setSysMsg")
    suspend fun CommandSender.setSysMsg(msg: String) {
        PluginConfig.systemMessage = msg
        sendMessage("Set system message to\n${PluginConfig.systemMessage}")
    }

    @SubCommand("getSysMsg")
    suspend fun CommandSender.getSysMsg() {
        sendMessage("System message is\n${PluginConfig.systemMessage}")
    }
}