package it.ohalee.minecraftgpt;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import it.ohalee.minecraftgpt.command.GPTCommand;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main extends JavaPlugin {

    private final ExecutorService executor;
    @Getter
    private Cache<Player, StringBuilder> cache;

    public Main() {
        executor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void onEnable() {
        cache = Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .executor(executor)
                .removalListener((key, value, cause) -> {
                    if (key != null && cause == RemovalCause.EXPIRED) {
                        ((Player) key).sendMessage("ChatGPT has disconnected.");
                    }
                })
                .build();

        getCommand("chatgpt").setExecutor(new GPTCommand(this));

        getLogger().info("Plugin enabled!");
    }

    @Override
    public void onDisable() {
        executor.shutdown();

        getLogger().info("Plugin disabled!");
    }

}
