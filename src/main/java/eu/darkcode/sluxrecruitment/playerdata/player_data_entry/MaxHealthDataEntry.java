package eu.darkcode.sluxrecruitment.playerdata.player_data_entry;

import com.google.gson.JsonObject;
import eu.darkcode.sluxrecruitment.Core;
import eu.darkcode.sluxrecruitment.utils.MethodResult;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MaxHealthDataEntry extends AbstractPlayerDataEntry {
    MaxHealthDataEntry() {
        super("max_health");
    }

    @Override
    public MethodResult save(@NotNull Core core, @NotNull Player player, @NotNull JsonObject element) {
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if(attribute == null) element.addProperty(getKey(), 20.0D);
        else element.addProperty(getKey(), attribute.getBaseValue());
        return MethodResult.success();
    }

    @Override
    public MethodResult pre_load(@NotNull Core core, @NotNull Player player) {
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if(attribute == null) player.resetMaxHealth(); // THIS SHOULD NOT HAPPEN
        else attribute.setBaseValue(20.0D);
        return MethodResult.success();
    }

    @Override
    public MethodResult load(@NotNull Core core, @NotNull Player player, @Nullable JsonObject element) {
        try{
            AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if(attribute == null) throw new NullPointerException("Attribute of player (GENERIC_MAX_HEALTH) is null");
            else{
                if(element == null) attribute.setBaseValue(20.0D);
                else attribute.setBaseValue(element.get(getKey()).getAsDouble());
            }
        }catch (Throwable e){
            return MethodResult.error(e);
        }
        return MethodResult.success();
    }
}
