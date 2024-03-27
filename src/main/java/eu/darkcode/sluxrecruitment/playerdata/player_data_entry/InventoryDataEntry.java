package eu.darkcode.sluxrecruitment.playerdata.player_data_entry;

import com.google.gson.JsonObject;
import eu.darkcode.sluxrecruitment.Core;
import eu.darkcode.sluxrecruitment.utils.ItemUtils;
import eu.darkcode.sluxrecruitment.utils.MethodResult;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class InventoryDataEntry extends AbstractPlayerDataEntry {
    InventoryDataEntry() {
        super("inventory");
    }

    @Override
    public MethodResult save(@NotNull Core core, @NotNull Player player, @NotNull JsonObject element) {
        JsonObject obj = new JsonObject();

        PlayerInventory inventory = player.getInventory();
        obj.addProperty("contents", ItemUtils.itemStackArrayToBase64(inventory.getStorageContents()));
        obj.addProperty("armor", ItemUtils.itemStackArrayToBase64(inventory.getArmorContents()));
        obj.addProperty("offHand", ItemUtils.itemStackToBase64(inventory.getItemInOffHand()));
        obj.addProperty("slot", inventory.getHeldItemSlot());

        element.add(getKey(), obj);
        return MethodResult.success();
    }

    public MethodResult pre_load(@NotNull Core core, @NotNull Player player) {
        player.getInventory().clear();
        player.getInventory().setHeldItemSlot(0);
        return MethodResult.success();
    }

    @Override
    public MethodResult load(@NotNull Core core, @NotNull Player player, @Nullable JsonObject element) {
        try{
            if(element == null) return MethodResult.success();
            else {
                JsonObject obj = element.getAsJsonObject(getKey());
                PlayerInventory inventory = player.getInventory();
                inventory.setStorageContents(ItemUtils.itemStackArrayFromBase64(obj.get("contents").getAsString()));
                inventory.setArmorContents(ItemUtils.itemStackArrayFromBase64(obj.get("armor").getAsString()));
                inventory.setItemInOffHand(ItemUtils.itemStackFromBase64(obj.get("offHand").getAsString()));
                inventory.setHeldItemSlot(obj.get("slot").getAsInt());
            }
        }catch (Throwable e){
            return MethodResult.error(e);
        }
        return MethodResult.success();
    }

}