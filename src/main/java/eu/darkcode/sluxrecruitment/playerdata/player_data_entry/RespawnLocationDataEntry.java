package eu.darkcode.sluxrecruitment.playerdata.player_data_entry;

import com.google.gson.JsonObject;
import eu.darkcode.sluxrecruitment.Core;
import eu.darkcode.sluxrecruitment.utils.MethodResult;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class RespawnLocationDataEntry extends AbstractPlayerDataEntry {
    RespawnLocationDataEntry() {
        super("bed_location");
    }

    @Override
    public MethodResult save(@NotNull Core core, @NotNull Player player, @NotNull JsonObject element) {
        Location location = player.getRespawnLocation();
        if (location == null)
            return MethodResult.success();
        JsonObject obj = new JsonObject();
        obj.addProperty("world", location.getWorld().getName());
        obj.addProperty("x", location.getX());
        obj.addProperty("y", location.getY());
        obj.addProperty("z", location.getZ());
        obj.addProperty("yaw", location.getYaw());
        obj.addProperty("pitch", location.getPitch());
        element.add(getKey(), obj);
        return MethodResult.success();
    }

    @Override
    public MethodResult pre_load(@NotNull Core core, @NotNull Player player) {
        player.setRespawnLocation(null);
        return MethodResult.success();
    }

    @Override
    public MethodResult load(@NotNull Core core, @NotNull Player player, @Nullable JsonObject element) {
        try{
            Bukkit.getScheduler().callSyncMethod(core, () -> {
                if (element == null) {
                    return pre_load(core, player);
                }else {
                    JsonObject obj = element.getAsJsonObject(getKey());
                    Location location = new Location(
                            Bukkit.getWorld(obj.get("world").getAsString()),
                            obj.get("x").getAsDouble(),
                            obj.get("y").getAsDouble(),
                            obj.get("z").getAsDouble(),
                            obj.get("yaw").getAsFloat(),
                            obj.get("pitch").getAsFloat());
                    player.setRespawnLocation(location);
                }
                return null;
            });
        }catch (Throwable e){
            return MethodResult.error(e);
        }
        return MethodResult.success();
    }
}
