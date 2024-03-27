package eu.darkcode.sluxrecruitment.playerdata.player_data_entry;

import com.google.gson.JsonObject;
import eu.darkcode.sluxrecruitment.Core;
import eu.darkcode.sluxrecruitment.utils.MethodResult;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class GameModeDataEntry extends AbstractPlayerDataEntry {
    GameModeDataEntry() {
        super("game_mode");
    }

    @Override
    public MethodResult save(@NotNull Core core, @NotNull Player player, @NotNull JsonObject element) {
        element.addProperty(getKey(), player.getGameMode().name());
        return MethodResult.success();
    }

    public MethodResult pre_load(@NotNull Core core, @NotNull Player player) {
        player.setGameMode(Bukkit.getDefaultGameMode());
        return MethodResult.success();
    }

    @Override
    public MethodResult load(@NotNull Core core, @NotNull Player player, @Nullable JsonObject element) {
        try{
            Bukkit.getScheduler().callSyncMethod(core, () -> {
                if(element == null) return MethodResult.success();
                else player.setGameMode(GameMode.valueOf(element.get(getKey()).getAsString()));
                return null;
            });
        }catch (Throwable e){
            return MethodResult.error(e);
        }
        return MethodResult.success();
    }
}
