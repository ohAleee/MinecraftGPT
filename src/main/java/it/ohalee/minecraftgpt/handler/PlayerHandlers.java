package it.ohalee.minecraftgpt.handler;

import it.ohalee.minecraftgpt.Main;
import it.ohalee.minecraftgpt.OpenAI;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
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

        Collection<Player> recipients = switch (Main.USER_TYPE.asMap().getOrDefault(player, hasFull ? Type.FULL : Type.SINGLE) ) {
            case SINGLE -> Collections.singletonList(player);
            case FULL, BROADCAST -> e.getRecipients();
        };

        List<String> list = plugin.getConfig().getStringList("format");

        if (!plugin.getConfig().getBoolean("use-default-chat", false)) {
            e.setCancelled(true);

            for (Player rec : recipients)
                rec.sendMessage(list.get(0).replace("&", "§")
                        .replace("%message%", e.getMessage())
                        .replace("%player%", player.getName()));
        }

        StringBuilder builder = Main.CACHE.getIfPresent(player);
        if (builder == null) builder = new StringBuilder();

        OpenAI.getResponse(builder, e.getMessage()).whenComplete((response, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                player.sendMessage(plugin.getConfig().getString("command.error").replace("&", "§"));
                return;
            }

            for (Player rec : recipients)
                rec.sendMessage(list.get(1).replace("&", "§")
                        .replace("%message%", response)
                        .replace("%player%", player.getName()));
        });
    }

}
