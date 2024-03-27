package eu.darkcode.sluxrecruitment.playerdata.player_data_entry;

import com.google.gson.JsonObject;
import eu.darkcode.sluxrecruitment.Core;
import eu.darkcode.sluxrecruitment.utils.MethodResult;
import eu.darkcode.sluxrecruitment.worldborder.WorldBorderListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class LocationDataEntry extends AbstractPlayerDataEntry {
    LocationDataEntry() {
        super("location");
    }

    @Override
    public MethodResult save(@NotNull Core core, @NotNull Player player, @NotNull JsonObject element) {
        JsonObject obj = new JsonObject();
        Location location = player.getLocation();
        obj.addProperty("world", location.getWorld().getName());
        obj.addProperty("x", location.getX());
        obj.addProperty("y", location.getY());
        obj.addProperty("z", location.getZ());
        obj.addProperty("yaw", location.getYaw());
        obj.addProperty("pitch", location.getPitch());
        element.add(getKey(), obj);
        return MethodResult.success();
    }

    public MethodResult pre_load(@NotNull Core core, @NotNull Player player) {
        // This is tough... should I teleport him to some safe place till he loads ?
        return MethodResult.success();
    }

    @Override
    public MethodResult load(@NotNull Core core, @NotNull Player player, @Nullable JsonObject element) {
        try{
            if(element == null) return MethodResult.success();
            else {
                JsonObject obj = element.getAsJsonObject(getKey());
                Location location = new Location(
                        Bukkit.getWorld(obj.get("world").getAsString()),
                        obj.get("x").getAsDouble(),
                        obj.get("y").getAsDouble(),
                        obj.get("z").getAsDouble(),
                        obj.get("yaw").getAsFloat(),
                        obj.get("pitch").getAsFloat());

                if(WorldBorderListener.teleportToSafety(location, player)){
                    player.teleportAsync(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
                }
            }
        }catch (Throwable e){
            return MethodResult.error(e);
        }
        return MethodResult.success();
    }
}
