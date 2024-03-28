package eu.darkcode.sluxrecruitment.worldborder;

import eu.darkcode.sluxrecruitment.utils.ComponentUtil;
import eu.darkcode.sluxrecruitment.utils.SoundUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class WorldBorderCommand implements TabExecutor {
    private final WorldBorderManager worldBorderManager;

    public WorldBorderCommand(WorldBorderManager worldBorderManager) {
        this.worldBorderManager = worldBorderManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!sender.hasPermission("slux.developer")) {
            sender.sendMessage(Bukkit.permissionMessage());
            return true;
        }

        if(args.length == 3 && args[0].equalsIgnoreCase("set")){
            double size;
            try {
                size = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ComponentUtil.legacy("&8[&cWorld Border&8] &cInvalid size!"));
                if(sender instanceof Player)
                    SoundUtil.playSound((Player) sender, Sound.ENTITY_VILLAGER_NO);
                return true;
            }
            if(size < 1){
                sender.sendMessage(ComponentUtil.legacy("&8[&cWorld Border&8] &cInvalid size!"));
                if(sender instanceof Player)
                    SoundUtil.playSound((Player) sender, Sound.ENTITY_VILLAGER_NO);
                return true;
            }
            
            World world = Bukkit.getWorld(args[1]);
            if(world == null) {
                sender.sendMessage(ComponentUtil.legacy("&8[&cWorld Border&8] &cInvalid world!"));
                return true;
            }
            
            
            if(worldBorderManager.addBorder(new WorldBorder(args[1], size))){
                sender.sendMessage(ComponentUtil.legacy("&8[&cWorld Border&8] &7World border has been set! &8(&c" + args[1] + " &7=> &c" + size + "&7x&c" + size + "&8)"));
                if(sender instanceof Player)
                    SoundUtil.playSound((Player) sender, Sound.ENTITY_VILLAGER_YES);
            }else {
                sender.sendMessage(ComponentUtil.legacy("&8[&cWorld Border&8] &cSomething went wrong!"));
                if(sender instanceof Player)
                    SoundUtil.playSound((Player) sender, Sound.ENTITY_VILLAGER_NO);
            }
            return true;
        }else if(args.length == 2 && args[0].equalsIgnoreCase("remove")){
            if (worldBorderManager.removeBorder(args[1])) {
                sender.sendMessage(ComponentUtil.legacy("&8[&cWorld Border&8] &7World border has been removed! &8(&c" + args[1] + "&8)"));
                if(sender instanceof Player)
                    SoundUtil.playSound((Player) sender, Sound.ENTITY_VILLAGER_YES);
            }else {
                sender.sendMessage(ComponentUtil.legacy("&8[&cWorld Border&8] &cSomething went wrong!"));
                if(sender instanceof Player)
                    SoundUtil.playSound((Player) sender, Sound.ENTITY_VILLAGER_NO);
            }
            return true;
        }
        sender.sendMessage(ComponentUtil.legacy("&8[&cWorld Border&8] &cInvalid usage! &8(&c/border set <world> <size> &8| &c/border remove <world> &8)"));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!sender.hasPermission("slux.developer"))
            return null;
        if(args.length == 1)
            return List.of("set", "remove");
        if(args.length == 2 && args[0].equalsIgnoreCase("set"))
            return Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList());
        if(args.length == 2 && args[0].equalsIgnoreCase("remove"))
            return new ArrayList<>(worldBorderManager.getBorders().keySet());
        return List.of();
    }
}
