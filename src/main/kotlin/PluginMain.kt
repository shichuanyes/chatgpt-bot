package com.github.shichuanyes.chatgpt

import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.permission.PermissionService
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.utils.info

object PluginMain : KotlinPlugin(
    JvmPluginDescription(
        id = "com.github.shichuanyes.chatgpt",
        name = "ChatGPT Bot",
        version = "0.3.0"
    ) {
        author("shichuanyes")
        info(
            """
            ChatGPT API as QQ/mirai plugin. 
        """.trimIndent()
        )
    }
) {

    override fun onEnable() {
        PluginConfig.reload()
        PluginData.reload()

        ChatGpt.register()

        logger.info { "Plugin loaded" }
        //配置文件目录 "${dataFolder.absolutePath}/"
        val eventChannel = GlobalEventChannel.parentScope(this)
        eventChannel.subscribeAlways<MessageEvent> {
            val content = message.contentToString()
            if (content.startsWith("!ask")) {
                subject.sendMessage(RequestHandler.getApiResponse(
                    prompt = content.replace("!ask", ""),
                    apiKey = PluginConfig.apiKey,
                    prevMessages = arrayListOf(Message(role = "system", content = PluginConfig.systemMessage))
                ))
            } else if (content.startsWith("!chat")) {
                val history = PluginData.msgHistory[sender.id]
                if (history.isEmpty()) {
                    history.add(Message(role = "system", content = PluginConfig.systemMessage))
                }
                val response = RequestHandler.getApiResponse(
                    prompt = content.replace("!chat", ""),
                    apiKey = PluginConfig.apiKey,
                    prevMessages = history
                )
                history.add(Message(role = "assistant", content = response))
                subject.sendMessage(response)
            } else if (content.startsWith("!clear")) {
                PluginData.msgHistory[sender.id] = arrayListOf()
                subject.sendMessage("Chat history cleared")
            }
        }

        modPermission // 注册权限
    }

    // region console 权限系统示例
    private val modPermission by lazy { // Lazy: Lazy 是必须的, console 不允许提前访问权限系统
        // 注册一条权限节点 org.example.mirai-example:my-permission
        // 并以 org.example.mirai-example:* 为父节点

        // @param: parent: 父权限
        //                 在 Console 内置权限系统中, 如果某人拥有父权限
        //                 那么意味着此人也拥有该权限 (org.example.mirai-example:my-permission)
        // @func: PermissionIdNamespace.permissionId: 根据插件 id 确定一条权限 id
        PermissionService.INSTANCE.register(permissionId("mod-permission"), "Moderator permission", parentPermission)
    }
}
