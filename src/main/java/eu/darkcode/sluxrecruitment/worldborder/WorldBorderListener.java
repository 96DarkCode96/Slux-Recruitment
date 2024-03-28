package eu.darkcode.sluxrecruitment.worldborder;

import eu.darkcode.sluxrecruitment.Core;
import eu.darkcode.sluxrecruitment.config.IPluginConfig;
import eu.darkcode.sluxrecruitment.utils.ComponentUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.configuration.file.YamlConfiguration;
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

    public static boolean teleportToSafety(Player player, double size){
        Location location = player.getLocation();
        WorldBorder worldBorder = location.getWorld().getWorldBorder();
        Location center = worldBorder.getCenter();

        return checkOutside(location, player, center, size);
    }

    /**
     *
     * @param location
     * @param player
     * @return false if player is outside
     */
    public static boolean teleportToSafety(Location location, Player player){
        WorldBorder worldBorder = location.getWorld().getWorldBorder();
        Location center = worldBorder.getCenter();
        double size = worldBorder.getSize();

        return checkOutside(location, player, center, size);
    }

    /**
     *
     * @param location
     * @param player
     * @param center
     * @param size
     * @return false if player is outside
     */
    private static boolean checkOutside(Location location, Player player, Location center, double size) {
        double size2 = size / 2;
        if(location.getX() > center.getX() + size2 || location.getX() < center.getX() - size2 ||
                location.getZ() > center.getZ() + size2 || location.getZ() < center.getZ() - size2) {


            WorldBorderManager worldBorderManager1 = Core.INSTANCE.getWorldBorderManager();
            IPluginConfig config = worldBorderManager1.getConfig();
            YamlConfiguration config1 = config.getConfig();
            assert config1 != null;
            Location spawn = config1.getLocation("spawn", location.getWorld().getSpawnLocation());
            player.teleportAsync(spawn);
            player.sendMessage(ComponentUtil.legacy("&8[&cServer&8] &7You have been teleported to the world spawn!\n" +
                    "&8[&cServer&8] &7Please make sure to stay within the world border!"));
            return false;
        }
        return true;
    }
}
