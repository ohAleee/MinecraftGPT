package it.ohalee.minecraftgpt;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalCause;
import com.google.common.cache.RemovalListener;
import com.launchableinc.openai.completion.chat.ChatMessage;
import it.ohalee.minecraftgpt.command.ChatCommand;
import it.ohalee.minecraftgpt.handler.ChatHandler;
import it.ohalee.minecraftgpt.handler.PlayerHandler;
import it.ohalee.minecraftgpt.util.Messages;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main extends JavaPlugin {

    public static Cache<Player, List<ChatMessage>> CACHE;
    public static Cache<Player, Type> USER_TYPE = CacheBuilder.newBuilder().build();

    @Override
    public void onEnable() {
        saveDefaultConfig();

        OpenAI.init(getConfig().getString("API_KEY")).exceptionallyAsync(throwable -> {
            getLogger().severe("Error while initializing OpenAI service! Is your API key valid?");
            throwable.printStackTrace();
            return null;
        });

        CACHE = CacheBuilder.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .removalListener((RemovalListener<Player, List<ChatMessage>>) notification -> {
                    if (notification.getKey() == null) return;
                    USER_TYPE.invalidate(notification.getKey());
                    if (notification.getCause() == RemovalCause.EXPIRED) {
                        notification.getKey().sendMessage(Messages.format(getConfig().getString("command.toggle.disabled")));
                    }
                }).build();

        String priority = getConfig().getString("chat-priority", "HIGH").toUpperCase();
        Class<AsyncPlayerChatEvent> eventClass = AsyncPlayerChatEvent.class;
        getServer().getPluginManager().registerEvent(eventClass, new ChatHandler(this), EventPriority.valueOf(priority), (listener, event) -> {
            try {
                listener.getClass().getMethod("onAsyncPlayerChat", eventClass).invoke(listener, event);
            } catch (InvocationTargetException ex) {
                throw new EventException(ex.getCause());
            } catch (Throwable t) {
                throw new EventException(t);
            }
        }, this);
        getServer().getPluginManager().registerEvents(new PlayerHandler(this), this);

        ChatCommand command = new ChatCommand(this);
        PluginCommand chatgpt = getCommand("chatgpt");
        chatgpt.setExecutor(command);
        chatgpt.setTabCompleter(command);

        getLogger().info("Plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin disabled!");
    }

}
