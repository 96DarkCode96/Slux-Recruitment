package eu.darkcode.sluxrecruitment.worldborder;

import eu.darkcode.sluxrecruitment.utils.ComponentUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
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

    public static boolean teleportToSafety(Location location, Player player){
        WorldBorder worldBorder = location.getWorld().getWorldBorder();
        Location center = worldBorder.getCenter();
        double size = worldBorder.getSize();

        if(location.getX() > center.getX() + size || location.getX() < center.getX() - size ||
                location.getZ() > center.getZ() + size || location.getZ() < center.getZ() - size) {

            player.teleportAsync(location.getWorld().getSpawnLocation());
            player.sendMessage(ComponentUtil.legacy("&8[&cServer&8] &7You have been teleported to the world spawn!\n" +
                    "&8[&cServer&8] &7Please make sure to stay within the world border!"));
            return false;
        }
        return true;
    }
}
