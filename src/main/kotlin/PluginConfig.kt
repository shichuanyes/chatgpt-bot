package com.github.shichuanyes.chatgpt

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value

object PluginConfig : AutoSavePluginConfig("config") {
    var apiKey: String by value("")
    var systemMessage: String by value("")
}