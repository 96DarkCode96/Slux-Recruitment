package eu.darkcode.sluxrecruitment.worldborder;

import eu.darkcode.sluxrecruitment.utils.ResponseMessage;
import eu.darkcode.sluxrecruitment.utils.SoundUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class WorldBorderCommand implements CommandExecutor, TabCompleter {
    private final WorldBorderManager worldBorderManager;

    public WorldBorderCommand(WorldBorderManager worldBorderManager) {
        this.worldBorderManager = worldBorderManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!sender.hasPermission("sluxrecruitment.command.border")) {
            sender.sendMessage(Bukkit.permissionMessage());
            return true;
        }
        if(args.length == 3 && args[0].equalsIgnoreCase("set")){
            double size;
            try {
                size = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ResponseMessage.COMMAND_WORLDBORDER_SET_FAILED_INVALID_SIZE
                        .make(worldBorderManager.getCore())
                        .replaceText(builder -> builder.match("%world%").replacement(args[1]))
                        .replaceText(builder -> builder.match("%size%").replacement(args[2]))
                );
                if(sender instanceof Player)
                    SoundUtil.playSound((Player) sender, Sound.ENTITY_VILLAGER_NO);
                return true;
            }
            if(size < 1){
                sender.sendMessage(ResponseMessage.COMMAND_WORLDBORDER_SET_FAILED_SMALL_SIZE
                        .make(worldBorderManager.getCore())
                        .replaceText(builder -> builder.match("%world%").replacement(args[1]))
                        .replaceText(builder -> builder.match("%size%").replacement(String.valueOf(size)))
                );
                if(sender instanceof Player)
                    SoundUtil.playSound((Player) sender, Sound.ENTITY_VILLAGER_NO);
                return true;
            }
            if(worldBorderManager.addBorder(new WorldBorder(args[1], size))){
                sender.sendMessage(ResponseMessage.COMMAND_WORLDBORDER_SET_SUCCESS
                        .make(worldBorderManager.getCore())
                        .replaceText(builder -> builder.match("%world%").replacement(args[1]))
                        .replaceText(builder -> builder.match("%size%").replacement(String.valueOf(size)))
                );
                if(sender instanceof Player)
                    SoundUtil.playSound((Player) sender, Sound.ENTITY_VILLAGER_YES);
            }else {
                sender.sendMessage(ResponseMessage.COMMAND_WORLDBORDER_SET_FAILED
                        .make(worldBorderManager.getCore())
                        .replaceText(builder -> builder.match("%world%").replacement(args[1]))
                        .replaceText(builder -> builder.match("%size%").replacement(String.valueOf(size)))
                );
                if(sender instanceof Player)
                    SoundUtil.playSound((Player) sender, Sound.ENTITY_VILLAGER_NO);
            }
            return true;
        }else if(args.length == 2 && args[0].equalsIgnoreCase("remove")){
            if (worldBorderManager.removeBorder(args[1])) {
                sender.sendMessage(ResponseMessage.COMMAND_WORLDBORDER_REMOVE_SUCCESS
                        .make(worldBorderManager.getCore())
                        .replaceText(builder -> builder.match("%world%").replacement(args[1]))
                );
                if(sender instanceof Player)
                    SoundUtil.playSound((Player) sender, Sound.ENTITY_VILLAGER_YES);
            }else {
                sender.sendMessage(ResponseMessage.COMMAND_WORLDBORDER_REMOVE_FAILED
                        .make(worldBorderManager.getCore())
                        .replaceText(builder -> builder.match("%world%").replacement(args[1]))
                );
                if(sender instanceof Player)
                    SoundUtil.playSound((Player) sender, Sound.ENTITY_VILLAGER_NO);
            }
            return true;
        }
        sender.sendMessage(ResponseMessage.COMMAND_WORLDBORDER_USAGE.make(worldBorderManager.getCore()));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!sender.hasPermission("sluxrecruitment.command.border"))
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
