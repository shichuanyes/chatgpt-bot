package com.github.shichuanyes.chatgpt

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.PluginDataExtensions.withEmptyDefault
import net.mamoe.mirai.console.data.value

object PluginData : AutoSavePluginData("data") {
    val msgHistory by value<MutableMap<Long, MutableList<Message>>>().withEmptyDefault()
}