package it.ohalee.minecraftgpt.handler;

import it.ohalee.minecraftgpt.Main;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class PlayerHandler implements Listener {

    private final Main plugin;

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Main.CACHE.invalidate(e.getPlayer());
    }

}
