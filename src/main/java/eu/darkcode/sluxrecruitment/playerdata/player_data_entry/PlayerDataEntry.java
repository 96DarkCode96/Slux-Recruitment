package eu.darkcode.sluxrecruitment.playerdata.player_data_entry;

import com.google.gson.JsonObject;
import eu.darkcode.sluxrecruitment.Core;
import eu.darkcode.sluxrecruitment.utils.MethodResult;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PlayerDataEntry {

    MethodResult save(@NotNull Core core, @NotNull Player player, @NotNull JsonObject element);
    MethodResult pre_load(@NotNull Core core, @NotNull Player player);
    MethodResult load(@NotNull Core core, @NotNull Player player, @Nullable JsonObject element);

    boolean canLoad(@Nullable JsonObject element);

    @NotNull String getKey();

}