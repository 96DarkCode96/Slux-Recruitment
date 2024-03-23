package eu.darkcode.sluxrecruitment.playerdata.player_data_entry;

import com.google.gson.JsonObject;
import eu.darkcode.sluxrecruitment.Core;
import eu.darkcode.sluxrecruitment.utils.MethodResult;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static eu.darkcode.sluxrecruitment.utils.InventoryUtils.deserializeInventoryContents;
import static eu.darkcode.sluxrecruitment.utils.InventoryUtils.serializeInventoryContents;

public final class EnderChestDataEntry extends AbstractPlayerDataEntry {
    EnderChestDataEntry() {
        super("ender_chest");
    }

    @Override
    public MethodResult save(@NotNull Core core, @NotNull Player player, @NotNull JsonObject element) {
        JsonObject obj = new JsonObject();

        Inventory inventory = player.getEnderChest();
        obj.add("contents", serializeInventoryContents(inventory.getStorageContents()));

        element.add(getKey(), obj);
        return MethodResult.success();
    }

    @Override
    public MethodResult pre_load(@NotNull Core core, @NotNull Player player) {
        player.getEnderChest().clear();
        return MethodResult.success();
    }

    @Override
    public MethodResult load(@NotNull Core core, @NotNull Player player, @Nullable JsonObject element) {
        try{
            if(element == null) return pre_load(core, player);
            else {
                JsonObject obj = element.getAsJsonObject(getKey());
                Inventory inventory = player.getEnderChest();
                inventory.setStorageContents(deserializeInventoryContents(obj.get("contents")));
            }
        }catch (Throwable e){
            return MethodResult.error(e);
        }
        return MethodResult.success();
    }

}