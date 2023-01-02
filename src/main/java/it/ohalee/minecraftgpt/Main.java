package it.ohalee.minecraftgpt;

import com.google.common.cache.*;
import it.ohalee.minecraftgpt.command.GPTCommand;
import it.ohalee.minecraftgpt.handler.PlayerHandlers;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.TimeUnit;

public class Main extends JavaPlugin {

    @Getter
    private Cache<Player, StringBuilder> cache;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        OpenAI.init(getConfig().getString("API_KEY"));

        cache = CacheBuilder.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .removalListener((RemovalListener<Player, StringBuilder>) notification -> {
                    if (notification.getCause() == RemovalCause.EXPIRED && notification.getKey() != null) {
                        notification.getKey().sendMessage(getConfig().getString("command.toggle.disabled").replace("&", "§"));
                    }
                }).build();

        getServer().getPluginManager().registerEvents(new PlayerHandlers(this), this);
        getCommand("chatgpt").setExecutor(new GPTCommand(this));

        getLogger().info("Plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin disabled!");
    }

}
