package eu.darkcode.sluxrecruitment.playerdata.player_data_entry;

import com.google.gson.JsonObject;
import eu.darkcode.sluxrecruitment.Core;
import eu.darkcode.sluxrecruitment.utils.MethodResult;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class FoodDataEntry extends AbstractPlayerDataEntry {
    FoodDataEntry() {
        super("food");
    }

    @Override
    public MethodResult save(@NotNull Core core, @NotNull Player player, @NotNull JsonObject element) {
        JsonObject obj = new JsonObject();
        obj.addProperty("food_level", player.getFoodLevel());
        obj.addProperty("exhaustion_level", player.getExhaustion());
        element.add(getKey(), obj);
        return MethodResult.success();
    }

    public MethodResult pre_load(@NotNull Core core, @NotNull Player player) {
        player.setFoodLevel(20);
        player.setExhaustion(0);
        return MethodResult.success();
    }

    @Override
    public MethodResult load(@NotNull Core core, @NotNull Player player, @Nullable JsonObject element) {
        try{
            if(element == null) return MethodResult.success();
            else {
                JsonObject data = element.getAsJsonObject(getKey());
                player.setFoodLevel(data.get("food_level").getAsInt());
                player.setExhaustion(data.get("exhaustion_level").getAsInt());
            }
        }catch (Throwable e){
            return MethodResult.error(e);
        }
        return MethodResult.success();
    }
}
