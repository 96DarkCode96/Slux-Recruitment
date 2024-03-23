package eu.darkcode.sluxrecruitment.worldborder;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

@Getter
public final class WorldBorder implements ConfigurationSerializable {

    private final @NotNull String worldName;
    private double size;

    public WorldBorder(@NotNull String worldName, double size) {
        Objects.requireNonNull(worldName, "worldName cannot be null!");
        if(size < 1) size = 1; // throw new IllegalArgumentException("Border size cannot be less than 1!");
        this.worldName = worldName;
        this.size = size;
    }

    public static WorldBorder deserialize(Object map) {
        try {
            if (map instanceof Map) {
                Map<?, ?> mapData = (Map<?, ?>) map;
                String worldName = (String) mapData.get("worldName");
                double size = ((Number) mapData.get("size")).doubleValue();
                return new WorldBorder(worldName, size);
            }
        } catch (Throwable e) {
            throw new IllegalArgumentException("Failed to deserialize WorldBorder!", e);
        }
        throw new IllegalArgumentException("Failed to deserialize WorldBorder!");
    }

    public void setSize(double size) {
        if(size < 1) throw new IllegalArgumentException("Border size cannot be less than 1!");
        this.size = size;
    }

    public boolean apply() {
        try {
            World world = Bukkit.getWorld(worldName);
            if(world == null) return false;
            world.getWorldBorder().reset();
            world.getWorldBorder().setSize(size);
        } catch (Throwable e) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to apply world border! (" + worldName + ")", e);
            return false;
        }
        return true;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("worldName", worldName);
        map.put("size", size);
        return map;
    }
}