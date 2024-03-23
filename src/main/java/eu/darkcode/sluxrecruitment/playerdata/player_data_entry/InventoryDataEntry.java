package eu.darkcode.sluxrecruitment.playerdata.player_data_entry;

import com.google.gson.JsonObject;
import eu.darkcode.sluxrecruitment.Core;
import eu.darkcode.sluxrecruitment.utils.MethodResult;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static eu.darkcode.sluxrecruitment.utils.InventoryUtils.*;

public final class InventoryDataEntry extends AbstractPlayerDataEntry {
    InventoryDataEntry() {
        super("inventory");
    }

    @Override
    public MethodResult save(@NotNull Core core, @NotNull Player player, @NotNull JsonObject element) {
        JsonObject obj = new JsonObject();

        PlayerInventory inventory = player.getInventory();
        obj.add("contents", serializeInventoryContents(inventory.getStorageContents()));
        obj.add("armor", serializeInventoryContents(inventory.getArmorContents()));
        obj.add("offHand", serializeItem(inventory.getItemInOffHand()));
        obj.addProperty("slot", inventory.getHeldItemSlot());

        element.add(getKey(), obj);
        return MethodResult.success();
    }

    @Override
    public MethodResult pre_load(@NotNull Core core, @NotNull Player player) {
        player.getInventory().clear();
        player.getInventory().setHeldItemSlot(0);
        return MethodResult.success();
    }

    @Override
    public MethodResult load(@NotNull Core core, @NotNull Player player, @Nullable JsonObject element) {
        try{
            if(element == null) return pre_load(core, player);
            else {
                JsonObject obj = element.getAsJsonObject(getKey());
                PlayerInventory inventory = player.getInventory();
                inventory.setStorageContents(deserializeInventoryContents(obj.get("contents")));
                inventory.setArmorContents(deserializeInventoryContents(obj.get("armor")));
                inventory.setItemInOffHand(deserializeItem(obj.get("offHand")));
                inventory.setHeldItemSlot(obj.get("slot").getAsInt());
            }
        }catch (Throwable e){
            return MethodResult.error(e);
        }
        return MethodResult.success();
    }

}