package it.ohalee.minecraftgpt.command;

import it.ohalee.minecraftgpt.Main;
import it.ohalee.minecraftgpt.Type;
import it.ohalee.minecraftgpt.conversation.TypeManager;
import it.ohalee.minecraftgpt.util.Messages;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class ChatCommand implements TabExecutor {

    private final Main plugin;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1 && sender.hasPermission("minecraftgpt.command.reload") && args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            sender.sendMessage(ChatColor.GREEN + "Config reloaded!");
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }

        Type type = Type.SINGLE;
        if (args.length >= 1) {
            type = Type.getType(args[0]);
            if (type == null) {
                player.sendMessage(Messages.format(plugin.getConfig().getString("command.invalid-type"))
                        .replace("{types}", String.join(", ", Arrays.stream(Type.values()).map(Enum::name).toArray(String[]::new))));
                return true;
            }
        }

        if (!player.hasPermission("minecraftgpt.command." + type.name().toLowerCase())) {
            player.sendMessage(Messages.format(plugin.getConfig().getString("command.no-permission")));
            return true;
        }

        TypeManager.startConversation(plugin, player, type);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return Arrays.stream(Type.values()).map(type -> type.name().toLowerCase()).toList();
    }


}
