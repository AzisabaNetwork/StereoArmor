package net.azisaba.stereoarmor.commands;

import net.azisaba.stereoarmor.StereoArmorPlugin;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GiveStereoArmorCommand implements TabExecutor {
    private final StereoArmorPlugin plugin;

    public GiveStereoArmorCommand(@NotNull StereoArmorPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        ItemStack stack = player.getInventory().getItemInMainHand();
        if (stack.getType() == Material.AIR) {
            sender.sendMessage("あいてむもって");
            return true;
        }
        ItemMeta meta = stack.getItemMeta();
        meta.getPersistentDataContainer().set(plugin.fileKey, PersistentDataType.STRING, String.join(" ", args).replaceAll("[^a-zA-Z0-9.\\-_]", "_"));
        stack.setItemMeta(meta);
        player.getInventory().setItemInMainHand(stack);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return plugin.getNbs().keySet().stream().filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
