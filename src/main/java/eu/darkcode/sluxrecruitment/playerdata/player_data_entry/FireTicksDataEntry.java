package eu.darkcode.sluxrecruitment.playerdata.player_data_entry;

import com.google.gson.JsonObject;
import eu.darkcode.sluxrecruitment.Core;
import eu.darkcode.sluxrecruitment.utils.MethodResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class FireTicksDataEntry extends AbstractPlayerDataEntry {
    FireTicksDataEntry() {
        super("fire_ticks");
    }

    @Override
    public MethodResult save(@NotNull Core core, @NotNull Player player, @NotNull JsonObject element) {
        element.addProperty(getKey(), player.getFireTicks());
        return MethodResult.success();
    }

    public MethodResult pre_load(@NotNull Core core, @NotNull Player player) {
        player.setFireTicks(0);
        return MethodResult.success();
    }

    @Override
    public MethodResult load(@NotNull Core core, @NotNull Player player, @Nullable JsonObject element) {
        try{
            Bukkit.getScheduler().callSyncMethod(core, () -> {
                if(element == null) return MethodResult.success();
                else player.setFireTicks(element.get(getKey()).getAsInt());
                return null;
            });
        }catch (Throwable e){
            return MethodResult.error(e);
        }
        return MethodResult.success();
    }
}
