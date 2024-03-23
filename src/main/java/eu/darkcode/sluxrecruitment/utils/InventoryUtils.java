package eu.darkcode.sluxrecruitment.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Base64;

public final class InventoryUtils {

    private InventoryUtils() {}

    public static JsonElement serializeInventoryContents(ItemStack[] storageContents) {
        JsonArray array = new JsonArray(storageContents.length);
        for (ItemStack storageContent : storageContents) {
            array.add(serializeItem(storageContent));
        }
        return array;
    }

    public static ItemStack[] deserializeInventoryContents(JsonElement element) {
        if(!element.isJsonArray()) return new ItemStack[0];
        JsonArray array = element.getAsJsonArray();
        ItemStack[] contents = new ItemStack[array.size()];
        for (int i = 0; i < array.size(); i++)
            contents[i] = deserializeItem(array.get(i));
        return contents;
    }

    public static JsonElement serializeItem(@Nullable ItemStack item) {
        if(item == null || item.isEmpty()) return JsonNull.INSTANCE;
        return new JsonPrimitive(Base64.getEncoder().encodeToString(item.serializeAsBytes()));
    }

    public static @Nullable ItemStack deserializeItem(JsonElement element) {
        if(element == null || !element.isJsonPrimitive()) return null;
        return ItemStack.deserializeBytes(Base64.getDecoder().decode(element.getAsString()));
    }

}