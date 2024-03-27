package eu.darkcode.sluxrecruitment.playerdata.player_data_entry;

import com.google.gson.JsonObject;
import eu.darkcode.sluxrecruitment.Core;
import eu.darkcode.sluxrecruitment.utils.MethodResult;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class FlyDataEntry extends AbstractPlayerDataEntry {
    FlyDataEntry() {
        super("fly");
    }

    @Override
    public MethodResult save(@NotNull Core core, @NotNull Player player, @NotNull JsonObject element) {
        JsonObject obj = new JsonObject();
        obj.addProperty("allow", player.getAllowFlight());
        obj.addProperty("flying", player.isFlying());
        element.add(getKey(), obj);
        return MethodResult.success();
    }

    public MethodResult pre_load(@NotNull Core core, @NotNull Player player) {
        player.setAllowFlight(false);
        player.setFlying(false);
        return MethodResult.success();
    }

    @Override
    public MethodResult load(@NotNull Core core, @NotNull Player player, @Nullable JsonObject element) {
        try{
            if(element == null) return MethodResult.success();
            else {
                JsonObject obj = element.getAsJsonObject(getKey());
                player.setAllowFlight(obj.get("allow").getAsBoolean());
                player.setFlying(obj.get("flying").getAsBoolean());
            }
        }catch (Throwable e){
            return MethodResult.error(e);
        }
        return MethodResult.success();
    }
}
