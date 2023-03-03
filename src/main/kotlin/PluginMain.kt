package com.github.shichuanyes.chatgpt

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.permission.AbstractPermitteeId
import net.mamoe.mirai.console.permission.PermissionService
import net.mamoe.mirai.console.permission.PermissionService.Companion.hasPermission
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.utils.info

/**
 * 使用 kotlin 版请把
 * `src/main/resources/META-INF.services/net.mamoe.mirai.console.plugin.jvm.JvmPlugin`
 * 文件内容改成 `org.example.mirai.plugin.PluginMain` 也就是当前主类全类名
 *
 * 使用 kotlin 可以把 java 源集删除不会对项目有影响
 *
 * 在 `settings.gradle.kts` 里改构建的插件名称、依赖库和插件版本
 *
 * 在该示例下的 [JvmPluginDescription] 修改插件名称，id和版本，etc
 *
 * 可以使用 `src/test/kotlin/RunMirai.kt` 在 ide 里直接调试，
 * 不用复制到 mirai-console-loader 或其他启动器中调试
 */

object PluginMain : KotlinPlugin(
    JvmPluginDescription(
        id = "com.github.shichuanyes.chatgpt",
        name = "ChatGPT Bot",
        version = "0.1.0"
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
    private val gson = Gson()

    @Throws(FuelError::class)
    override fun onEnable() {
        PluginConfig.reload()
        PluginData.reload()

        Chatgpt.register()

        logger.info { "Plugin loaded" }
        //配置文件目录 "${dataFolder.absolutePath}/"
        val eventChannel = GlobalEventChannel.parentScope(this)
        eventChannel.subscribeAlways<GroupMessageEvent> {
            if (message.contentToString().startsWith(("!ask"))) {
                val body = RequestJson(messages = arrayListOf<Message>(Message(role = "user", content = message.contentToString().replace("!ask", ""))))
                val (_, response, result) = Fuel.post("https://api.openai.com/v1/chat/completions")
                    .authentication()
                    .bearer(PluginConfig.apiKey)
                    .jsonBody(gson.toJson(body))
                    .responseString()
                if (result is Result.Failure) throw result.getException()
                val json = gson.fromJson(response.data.decodeToString(), ResponseJson::class.java)
                group.sendMessage(json.choices.first().message.content)
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

    public fun hasModPermission(sender: User): Boolean {
        return when (sender) {
            is Member -> AbstractPermitteeId.ExactMember(sender.group.id, sender.id)
            else -> AbstractPermitteeId.ExactUser(sender.id)
        }.hasPermission(modPermission)
    }
    // endregion
}
