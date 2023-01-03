package it.ohalee.minecraftgpt.conversation;

import it.ohalee.minecraftgpt.Main;
import it.ohalee.minecraftgpt.Type;
import org.bukkit.entity.Player;

public class TypeManager {

    public static void startConversation(Main plugin, Player player, Type type) {
        if (Main.CACHE.asMap().containsKey(player)) {
            Main.CACHE.invalidate(player);
            player.sendMessage(plugin.getConfig().getString("command.toggle.disabled").replace("&", "§"));
            return;
        }

        Main.USER_TYPE.put(player, type);
        Main.CACHE.put(player, new StringBuilder());
        player.sendMessage(plugin.getConfig().getString("command.toggle.enabled").replace("&", "§"));
    }

}
