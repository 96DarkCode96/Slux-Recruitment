package eu.darkcode.sluxrecruitment.playerdata.player_data_entry;

import com.google.gson.JsonObject;
import eu.darkcode.sluxrecruitment.Core;
import eu.darkcode.sluxrecruitment.utils.MethodResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

import static eu.darkcode.sluxrecruitment.utils.PotionEffectsUtils.deserializeEffects;
import static eu.darkcode.sluxrecruitment.utils.PotionEffectsUtils.serializeEffects;

public final class EffectsDataEntry extends AbstractPlayerDataEntry {
    EffectsDataEntry() {
        super("effects");
    }

    @Override
    public MethodResult save(@NotNull Core core, @NotNull Player player, @NotNull JsonObject element) {
        element.add(getKey(), serializeEffects(player.getActivePotionEffects()));
        return MethodResult.success();
    }

    public MethodResult pre_load(@NotNull Core core, @NotNull Player player) {
        player.clearActivePotionEffects();
        return MethodResult.success();
    }

    @Override
    public MethodResult load(@NotNull Core core, @NotNull Player player, @Nullable JsonObject element) {
        try{
            Bukkit.getScheduler().callSyncMethod(core, () -> {
                if(element == null) return MethodResult.success();
                else {
                    Collection<PotionEffect> potionEffects = deserializeEffects(element.get(getKey()));
                    if(potionEffects.isEmpty()) return MethodResult.success();
                    player.addPotionEffects(potionEffects);
                    return null;
                }
            });
        }catch (Throwable e){
            return MethodResult.error(e);
        }
        return MethodResult.success();
    }

}