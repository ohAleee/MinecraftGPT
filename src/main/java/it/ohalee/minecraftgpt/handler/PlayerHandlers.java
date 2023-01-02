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

import java.util.List;

@RequiredArgsConstructor
public class PlayerHandlers implements Listener {

    private final Main plugin;

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        plugin.getCache().invalidate(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        if (this.plugin.getCache().asMap().containsKey(player)) {
            e.setCancelled(true);

            List<String> list = plugin.getConfig().getStringList("command.format");

            player.sendMessage(list.get(0).replace("&", "§").replace("%message%", e.getMessage()));
            OpenAI.getResponse(plugin.getCache().getIfPresent(player), e.getMessage()).whenComplete((response, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                    player.sendMessage(plugin.getConfig().getString("command.error").replace("&", "§"));
                    return;
                }
                player.sendMessage(list.get(1).replace("&", "§").replace("%message%", response));
            });
        }
    }

}
