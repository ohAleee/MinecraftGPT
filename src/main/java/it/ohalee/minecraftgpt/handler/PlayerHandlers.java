package it.ohalee.minecraftgpt.handler;

import it.ohalee.minecraftgpt.Main;
import it.ohalee.minecraftgpt.OpenAI;
import it.ohalee.minecraftgpt.Type;
import it.ohalee.minecraftgpt.util.Messages;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class PlayerHandlers implements Listener {

    private final Main plugin;

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Main.CACHE.invalidate(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();

        boolean hasFull = Main.USER_TYPE.asMap().values().stream().anyMatch(type -> type == Type.FULL);
        if (!Main.CACHE.asMap().containsKey(player) && !hasFull) {
            return;
        }

        Collection<Player> recipients = switch (Main.USER_TYPE.asMap().getOrDefault(player, hasFull ? Type.FULL : Type.SINGLE)) {
            case SINGLE -> Collections.singletonList(player);
            case FULL, BROADCAST -> e.getRecipients();
        };

        List<String> list = plugin.getConfig().getStringList("format");

        if (!plugin.getConfig().getBoolean("use-default-chat", false)) {
            e.setCancelled(true);

            sendMessage(format(list.get(0), e.getMessage(), player.getName()), recipients);
        }

        StringBuilder builder = Main.CACHE.getIfPresent(player);
        if (builder == null) builder = new StringBuilder();

        OpenAI.getResponse(plugin.getConfig().getConfigurationSection("chatgpt"), builder, e.getMessage()).whenComplete((response, throwable) -> {
            if (response == null) {
                player.sendMessage(Messages.format(plugin.getConfig().getString("command.error")));
                return;
            }
            sendMessage(format(list.get(1), response, player.getName()), recipients);
        });
    }

    private String format(String str, String message, String player) {
        return Messages.format(str).replace("%message%", message).replace("%player%", player);
    }

    private void sendMessage(String message, Collection<Player> players) {
        for (Player player : players)
            player.sendMessage(message);
        if (plugin.getConfig().getBoolean("send-messages-to-console", true))
            plugin.getServer().getConsoleSender().sendMessage(message);
    }

}
