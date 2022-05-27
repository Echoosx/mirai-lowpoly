package org.echoosx.mirai.plugin

import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.utils.info
import org.echoosx.mirai.plugin.command.GenerateLowPolyCommand

object MiraiLowPoly : KotlinPlugin(
    JvmPluginDescription(
        id = "org.echoosx.mirai.plugin.mirai-lowpoly",
        name = "mirai-lowpoly",
        version = "0.1.0"
    ) {
        author("Echoosx")
    }
) {
    override fun onEnable() {
        logger.info { "Plugin loaded" }
        GenerateLowPolyCommand.register()
    }
}
