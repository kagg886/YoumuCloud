package com.kagg886.youmucloud_mirai;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.console.data.AbstractPluginData;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.utils.MiraiLogger;

public final class PluginInstance extends JavaPlugin {
    public static final PluginInstance INSTANCE = new PluginInstance();
    public static final MiraiLogger logger = INSTANCE.getLogger();
    public static int vercode = 20230109;
    private PluginInstance() {
        super(new JvmPluginDescriptionBuilder("com.kagg886.youmucloud_mirai", "2.5")
                .name("YoumuCloud")
                .info("Youmucloud_mirai实现\n吹水群:973510746")
                .author("kagg886")
                .build());
    }

    @Override
    public void onEnable() {
        reloadPluginConfig(YoumuConfig.INSTANCE);
        GlobalEventChannel.INSTANCE.registerListenerHost(new MsgHandler());
    }
}