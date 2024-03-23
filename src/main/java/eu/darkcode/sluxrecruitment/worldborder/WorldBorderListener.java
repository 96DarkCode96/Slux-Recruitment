package eu.darkcode.sluxrecruitment.worldborder;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

@Getter
public class WorldBorderListener implements Listener {
    private final WorldBorderManager worldBorderManager;

    public WorldBorderListener(WorldBorderManager worldBorderManager) {
        this.worldBorderManager = worldBorderManager;
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        worldBorderManager.ifExists(event.getWorld().getName(), (border) -> {
            if (!border.apply())
                Bukkit.getLogger().warning("Failed to apply world border for " + event.getWorld().getName());
        });
    }

}
