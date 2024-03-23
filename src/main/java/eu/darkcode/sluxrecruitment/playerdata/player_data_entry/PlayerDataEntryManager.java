package eu.darkcode.sluxrecruitment.playerdata.player_data_entry;

import java.util.Collection;
import java.util.List;

public final class PlayerDataEntryManager {

    public static final PlayerDataEntry GAME_MODE = new GameModeDataEntry();
    public static final PlayerDataEntry MAX_HEALTH = new MaxHealthDataEntry();
    public static final PlayerDataEntry HEALTH = new HealthDataEntry();
    public static final PlayerDataEntry FOOD = new FoodDataEntry();
    public static final PlayerDataEntry SATURATION = new SaturationDataEntry();
    public static final PlayerDataEntry FLY = new FlyDataEntry();
    public static final PlayerDataEntry LOCATION = new LocationDataEntry();
    public static final PlayerDataEntry EXP = new ExpDataEntry();
    public static final PlayerDataEntry INVENTORY = new InventoryDataEntry();
    public static final PlayerDataEntry EFFECTS = new EffectsDataEntry();
    public static final PlayerDataEntry ENDER_CHEST = new EnderChestDataEntry();
    public static final PlayerDataEntry AIR_LEVEL = new AirLevelDataEntry();
    public static final PlayerDataEntry RESPAWN_LOCATION = new RespawnLocationDataEntry();
    public static final PlayerDataEntry SEEN_CREDITS = new SeenCreditsDataEntry();
    public static final PlayerDataEntry PORTAL_COOLDOWN = new PortalCooldownDataEntry();
    public static final PlayerDataEntry ABSORPTION = new AbsorptionDataEntry();
    public static final PlayerDataEntry FIRE_TICKS = new FireTicksDataEntry();
    public static final PlayerDataEntry TICKS_LIVED = new TicksLivedDataEntry();
    public static final PlayerDataEntry DISCOVERED_RECIPES = new DiscoveredRecipesDataEntry();

    public static final Collection<PlayerDataEntry> entries = List.of(
            MAX_HEALTH, HEALTH, GAME_MODE, FOOD, SATURATION, FLY, LOCATION,
            EXP, INVENTORY, EFFECTS, ENDER_CHEST, AIR_LEVEL, RESPAWN_LOCATION,
            SEEN_CREDITS, PORTAL_COOLDOWN, ABSORPTION, DISCOVERED_RECIPES, FIRE_TICKS, TICKS_LIVED);

    private PlayerDataEntryManager() {}

}