package net.mikoto.pixiv.jpbc.mirai.plugin

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.Listener
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mikoto.pixiv.jpbc.mirai.plugin.JpbcConfig.QQ
import net.mikoto.pixiv.jpbc.mirai.plugin.bot.BotManager
import net.mikoto.pixiv.jpbc.mirai.plugin.event.PixivCommandEvent

/**
 * @author  mikoto
 * @date  2021/11/28 5:59
 */

const val VERSION = "1.0.0"
const val DESCRIPTION = "A Java Pixiv Bot Connectivity"
const val AUTHOR = "mikoto"
const val PACKAGE = "net.mikoto.pixiv.jpbc.mirai.plugin"

object JpbcMiraiPlugin : KotlinPlugin(
    JvmPluginDescription(
        id = PACKAGE,
        version = VERSION,
    ) {
        name("Java Bot Connectivity")
        info(DESCRIPTION)
        author(AUTHOR)
    }
) {
    private var listener: Listener<GroupMessageEvent>? = null

    override fun onEnable() {
        JpbcConfig.reload()
        val bots = QQ.split(";").toTypedArray()
        for (bot in bots) {
            val botArray = bot.split("::").toTypedArray()
            BotManager.getInstance()
                .createBot(botArray[0].toLong(), botArray[1])
        }
        listener = PixivCommandEvent.getInstance().registerEvent();
    }

    override fun onDisable() {
        listener?.complete()
    }
}

object JpbcConfig : AutoSavePluginConfig("config") {
    val DISPLAYER_URL: String by value()
    val PICTURE_FORWARD_SERVER: String by value()
    val KEY: String by value()
    val QQ: String by value()
    val R_18: Boolean by value()
}

