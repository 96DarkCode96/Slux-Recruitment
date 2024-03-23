package eu.darkcode.sluxrecruitment.playerdata.player_data_entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eu.darkcode.sluxrecruitment.Core;
import eu.darkcode.sluxrecruitment.utils.MethodResult;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DiscoveredRecipesDataEntry extends AbstractPlayerDataEntry {
    DiscoveredRecipesDataEntry() {
        super("discovered_recipes");
    }

    @Override
    public MethodResult save(@NotNull Core core, @NotNull Player player, @NotNull JsonObject element) {
        JsonArray obj = new JsonArray();
        for (NamespacedKey discoveredRecipe : player.getDiscoveredRecipes())
            obj.add(discoveredRecipe.asString());
        element.add(getKey(), obj);
        return MethodResult.success();
    }

    @Override
    public MethodResult pre_load(@NotNull Core core, @NotNull Player player) {
        //player.undiscoverRecipes(player.getDiscoveredRecipes()); // THIS WOULD BE OKAY IF THERE WASN'T CLIENT DISPLAY GLITCH WHEN REASSIGNING RECIPES
        return MethodResult.success();
    }

    @Override
    public MethodResult load(@NotNull Core core, @NotNull Player player, @Nullable JsonObject element) {
        try{
            Bukkit.getScheduler().callSyncMethod(core, () -> {
                if(element == null) return pre_load(core, player);
                else {
                    JsonArray array = element.getAsJsonArray(getKey());
                    for (JsonElement jsonElement : array) {
                        NamespacedKey recipe = NamespacedKey.fromString(jsonElement.getAsString());
                        if(recipe == null) continue; // MAYBE ERROR HANDLING ?
                        player.discoverRecipe(recipe);
                    }
                }
                return null;
            });
        }catch (Throwable e){
            return MethodResult.error(e);
        }
        return MethodResult.success();
    }
}
