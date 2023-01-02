package it.ohalee.minecraftgpt.command;

import it.ohalee.minecraftgpt.Main;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class GPTCommand implements CommandExecutor {

    private final Main plugin;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }

        if (!player.hasPermission("minecraftgpt.command")) {
            player.sendMessage(plugin.getConfig().getString("command.no-permission").replace("&", "§"));
            return true;
        }

        if (plugin.getCache().asMap().containsKey(player)) {
            plugin.getCache().invalidate(player);
            player.sendMessage(plugin.getConfig().getString("command.toggle.disabled").replace("&", "§"));
            return true;
        }

        plugin.getCache().put(player, new StringBuilder());
        player.sendMessage(plugin.getConfig().getString("command.toggle.enabled").replace("&", "§"));
        return true;
    }

}
