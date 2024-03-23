package eu.darkcode.sluxrecruitment.playerdata;

import com.google.gson.JsonObject;
import eu.darkcode.sluxrecruitment.playerdata.player_data_entry.PlayerDataEntry;
import eu.darkcode.sluxrecruitment.playerdata.player_data_entry.PlayerDataEntryManager;
import eu.darkcode.sluxrecruitment.utils.MethodResult;
import eu.darkcode.sluxrecruitment.utils.ResponseMessage;
import eu.darkcode.sluxrecruitment.utils.SoundUtil;
import io.papermc.paper.event.player.AsyncChatEvent;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Getter
public class PlayerDataListener implements Listener {

    private final PlayerDataManager playerDataManager;
    private final Map<Player, ScheduledTask> loadingPlayers = new HashMap<>();
    private final Map<Player, Entity> loadingPlayersEntity = new HashMap<>();

    public PlayerDataListener(PlayerDataManager playerDataManager) {
        this.playerDataManager = playerDataManager;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // NOTIFY PLAYER OF LOADING
        SoundUtil.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT);
        player.sendMessage(playerDataManager.getCore().getResponseMessageManager().get(ResponseMessage.LOAD_PLAYER_DATA));

        // CLEAR PLAYER DATA LOADED FROM DEFAULT MC FILE !!!
        PlayerDataEntryManager.entries.forEach(entry -> entry.pre_load(playerDataManager.getCore(), player));
        player.setCanPickupItems(false); // Prevent player from picking up items cause of inventory overriding

        // SPAWN PIG AND FORCE PLAYER TO SIT ON IT
        spawnPig(player);

        // START ASYNC LOADING OF DATA
        loadingPlayers.put(player, Bukkit.getAsyncScheduler().runNow(playerDataManager.getCore(), scheduledTask -> {
            // LOAD PLAYER DATA
            JsonObject playerData = playerDataManager.getPlayerData(player.getName(), player.getUniqueId());

            List<PlayerDataEntry> dataEntries = PlayerDataEntryManager.entries.stream()
                    .filter(entry -> entry.canLoad(playerData))
                    .collect(Collectors.toList());

            for (PlayerDataEntry entry : dataEntries) {
                MethodResult load = entry.load(playerDataManager.getCore(), player, playerData);
                if(load.isSuccess())
                    continue;

                // REMOVE FROM LOADING LIST
                loadingPlayers.remove(player);

                // DE-SPAWN PIG
                despawnPig(player);

                player.setCanPickupItems(true);

                // KICK PLAYER
                Bukkit.getScheduler().callSyncMethod(playerDataManager.getCore(), () -> {
                    player.kick(playerDataManager.getCore().getResponseMessageManager().get(ResponseMessage.INVALID_JSON), PlayerKickEvent.Cause.RESTART_COMMAND);
                    return null;
                });

                // LOG FAILURE
                if(load.hasError())
                    Bukkit.getLogger().log(Level.SEVERE, "Failed to load player data! (" + player.getName() + ") (Entry: " + entry.getClass().getName() + ")", load.getError());
                else
                    Bukkit.getLogger().log(Level.SEVERE, "Failed to load player data! (" + player.getName() + ") (Entry: " + entry.getClass().getName() + ")");
                return;
            }

            // NOTIFY PLAYER OF SUCCESS
            SoundUtil.playSound(player, Sound.ENTITY_VILLAGER_YES);
            player.sendMessage(playerDataManager.getCore().getResponseMessageManager().get(ResponseMessage.LOAD_PLAYER_DATA_SUCCESS));

            // REMOVE FROM LOADING LIST
            loadingPlayers.remove(player);

            // DE-SPAWN PIG
            despawnPig(player);

            player.setCanPickupItems(true);
        }));
    }

    private void spawnPig(Player player) {
        Pig pig = (Pig) player.getWorld().spawnEntity(player.getLocation(), EntityType.PIG, CreatureSpawnEvent.SpawnReason.CUSTOM);
        pig.setAdult();
        pig.setAI(false);
        pig.setAgeLock(true);
        pig.setCollidable(false);
        pig.setCanPickupItems(false);
        pig.setGravity(false);
        pig.setInvisible(true);
        pig.setInvulnerable(true);
        pig.setSilent(true);

        pig.addPassenger(player);

        loadingPlayersEntity.put(player, pig);
    }

    private void despawnPig(Player player) {
        Entity entity = loadingPlayersEntity.remove(player);
        if(entity != null) {
            Bukkit.getScheduler().callSyncMethod(playerDataManager.getCore(), () -> {
                entity.getPassengers().forEach(entity::removePassenger);
                entity.remove();
                return null;
            });
        }
    }

    @EventHandler
    public void onVehicleExit(EntityDismountEvent event) {
        if(event.getEntityType() != EntityType.PLAYER)
            return;
        Player player = loadingPlayers.keySet().stream().filter(a -> a.getEntityId() == event.getEntity().getEntityId()).findFirst().orElse(null);
        if(player == null)
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (loadingPlayers.containsKey(event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onInteract(AsyncChatEvent event) {
        if (loadingPlayers.containsKey(event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onInteract(EntityDamageEvent event) {
        if(event.getEntityType() != EntityType.PLAYER)
            return;
        Player player = loadingPlayers.keySet().stream().filter(a -> a.getEntityId() == event.getEntity().getEntityId()).findFirst().orElse(null);
        if(player == null)
            return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        if(event.getReason().equals(PlayerQuitEvent.QuitReason.KICKED))
            return;
        stopLoading(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerKick(PlayerKickEvent event) {
        if(event.getCause().equals(PlayerKickEvent.Cause.RESTART_COMMAND))
            return;
        stopLoading(event.getPlayer());
    }

    private void stopLoading(Player player) {
        ScheduledTask remove = loadingPlayers.remove(player);
        if(remove != null) {
            remove.cancel();
        }else{
            playerDataManager.savePlayerData(player.getName(), player.getUniqueId(), playerDataManager.fetch(player));
        }
        Entity entity = loadingPlayersEntity.remove(player);
        if(entity != null) entity.remove();
    }
}