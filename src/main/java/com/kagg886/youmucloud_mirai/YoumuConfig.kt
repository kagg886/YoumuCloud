package com.kagg886.youmucloud_mirai

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value

object YoumuConfig : AutoSavePluginConfig("Config") {
    val isDisableTheDataLog by value<Boolean>(false)
    val Header by value<String>("{}")
    val ServerAddress by value<String>("ws://youmucloud.kagg886.top/youmu/api/");
}