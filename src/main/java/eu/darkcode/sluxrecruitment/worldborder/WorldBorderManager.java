package eu.darkcode.sluxrecruitment.worldborder;

import eu.darkcode.sluxrecruitment.Core;
import eu.darkcode.sluxrecruitment.config.IPluginConfig;
import eu.darkcode.sluxrecruitment.config.PluginConfig;
import eu.darkcode.sluxrecruitment.utils.MethodResult;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter
public final class WorldBorderManager {

    private final @NotNull Core core;
    private final @NotNull Map<String, WorldBorder> borders = new HashMap<>();
    private final @NotNull IPluginConfig config;

    public WorldBorderManager(@NotNull Core core) {
        this.core = core;

        ConfigurationSerialization.registerClass(WorldBorder.class);
        this.config = PluginConfig.of("borders.yml");

        MethodResult methodResult = loadBorders();
        if (!methodResult.isSuccess()) {
            if (methodResult.hasError()) {
                throw new RuntimeException("Failed to load borders", methodResult.getError());
            } else {
                throw new RuntimeException("Failed to load borders");
            }
        }

        Bukkit.getPluginManager().registerEvents(new WorldBorderListener(this), core);
        PluginCommand border = core.getCommand("border");
        if(border == null) {
            Bukkit.getLogger().warning("Failed to register border command! World border can still be edited in borders.yml");
        }else{
            border.setExecutor(new WorldBorderCommand(this));
            border.setTabCompleter(new WorldBorderCommand(this));
        }
    }

    public MethodResult loadBorders() {
        if (!this.config.loadConfig()) return MethodResult.error(new RuntimeException("Failed to load borders.yml"));

        try {
            assert config.getConfig() != null;
            config.getConfig().addDefault("borders", new ArrayList<>());
            config.getConfig().options().copyDefaults(true).setHeader(List.of("Slux-Recruitment", "", "For better expiration, use the 'border' command!", "",
                    "Warning: Please avoid manually editing the 'borders.yml' file! If you do something wrong, the borders will be reset! (Or maybe some of them!)", ""));
            config.saveConfig();

            List<?> list = config.getConfig().getList("borders", new ArrayList<>());
            list.stream().map(WorldBorder::deserialize).forEach(worldBorder -> {
                if(!addBorder(worldBorder))
                    Bukkit.getLogger().warning("Failed to load border: " + worldBorder.getWorldName());
            });
        } catch (Throwable e) {
            return MethodResult.error(e);
        }
        return MethodResult.success();
    }

    public MethodResult reloadBorders() {
        borders.keySet().forEach(this::prepareForReload);
        borders.clear();
        return loadBorders();
    }

    private void prepareForReload(String worldName) {
        World world = Bukkit.getWorld(worldName);
        if(world != null) world.getWorldBorder().reset();
    }

    public boolean addBorder(WorldBorder worldBorder) {
        borders.put(worldBorder.getWorldName(), worldBorder);
        boolean b = saveBorders();
        if(b) {
            worldBorder.apply();
        }else{
            borders.remove(worldBorder.getWorldName());
        }
        return b;
    }

    public boolean removeBorder(String worldName) {
        if(!borders.containsKey(worldName)) return false;
        WorldBorder remove = borders.remove(worldName);
        boolean b = saveBorders();
        if(b) {
            World world = Bukkit.getWorld(worldName);
            if(world != null) world.getWorldBorder().reset();
        }else{
            borders.put(worldName, remove);
        }
        return b;
    }

    private boolean saveBorders() {
        assert config.getConfig() != null;
        config.getConfig().set("borders", borders.values().stream().map(WorldBorder::serialize).collect(Collectors.toList()));
        return config.saveConfig();
    }

    public void ifExists(String name, Consumer<WorldBorder> o) {
        if(borders.containsKey(name)) o.accept(borders.get(name));
    }
}