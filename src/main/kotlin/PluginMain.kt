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
        version = "0.2.1"
    ) {
        author("shichuanyes")
        info(
            """
            ChatGPT API as QQ/mirai plugin. 
        """.trimIndent()
        )
        // author 和 info 可以删除.
    }
) {

    override fun onEnable() {
        PluginConfig.reload()
        PluginData.reload()

        Chatgpt.register()

        logger.info { "Plugin loaded" }
        //配置文件目录 "${dataFolder.absolutePath}/"
        val eventChannel = GlobalEventChannel.parentScope(this)
        eventChannel.subscribeAlways<MessageEvent> {
            val content = message.contentToString()
            if (content.startsWith("!ask")) {
                subject.sendMessage(RequestHandler.getApiResponse(
                    prompt = content.replace("!ask", ""),
                    apiKey = PluginConfig.apiKey,
                    systemMessage = PluginConfig.systemMessage
                ))
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
    // endregion
}
