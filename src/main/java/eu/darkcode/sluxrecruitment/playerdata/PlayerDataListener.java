package eu.darkcode.sluxrecruitment.playerdata;

import com.google.gson.JsonObject;
import eu.darkcode.sluxrecruitment.playerdata.player_data_entry.PlayerDataEntry;
import eu.darkcode.sluxrecruitment.playerdata.player_data_entry.PlayerDataEntryManager;
import eu.darkcode.sluxrecruitment.utils.ComponentUtil;
import eu.darkcode.sluxrecruitment.utils.MethodResult;
import eu.darkcode.sluxrecruitment.utils.SoundUtil;
import io.papermc.paper.event.player.AsyncChatEvent;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import lombok.Getter;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffectType;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Getter
public class PlayerDataListener implements Listener {

    private final PlayerDataManager playerDataManager;
    private final Map<UUID, ScheduledTask> loadingPlayers = new HashMap<>();

    public PlayerDataListener(PlayerDataManager playerDataManager) {
        this.playerDataManager = playerDataManager;
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // NOTIFY PLAYER OF LOADING
        SoundUtil.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT);
        player.sendMessage(ComponentUtil.legacy("&8[&cServer&8] &7Loading..."));

        // CLEAR PLAYER DATA LOADED FROM DEFAULT MC FILE !!!

        //PlayerDataEntryManager.entries.forEach(entry -> entry.pre_load(playerDataManager.getCore(), player));
        // REMOVED CAUSE SERVER IS RUNNING AND PLAYERS ALREADY HAVE SOME ITEMS IN MINECRAFT DATA FILE (THIS WOULD REMOVE ALL ITEMS FROM THEM)

        player.setCanPickupItems(false); // Prevent player from picking up items cause of inventory overriding
        player.addPotionEffect(PotionEffectType.DARKNESS.createEffect(-1, 0));
        player.showTitle(Title.title(ComponentUtil.legacy("&7Loading..."),
                ComponentUtil.legacy("&k&7# &r&8This may take a while &k&7#"),
                Title.Times.times(Duration.ZERO, Duration.ofDays(1), Duration.ZERO)));

        // START ASYNC LOADING OF DATA
        loadingPlayers.put(player.getUniqueId(), Bukkit.getAsyncScheduler().runNow(playerDataManager.getCore(), scheduledTask -> {
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
                loadingPlayers.remove(player.getUniqueId());

                player.setCanPickupItems(true);

                // KICK PLAYER
                Bukkit.getScheduler().callSyncMethod(playerDataManager.getCore(), () -> {
                    player.kick(ComponentUtil.legacy("&8[&cServer&8] &7Failed to load your data!"), PlayerKickEvent.Cause.RESTART_COMMAND);
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
            player.sendMessage(ComponentUtil.legacy("&8[&cServer&8] &7Successfully loaded your data!"));

            // REMOVE FROM LOADING LIST
            loadingPlayers.remove(player.getUniqueId());

            player.setCanPickupItems(true);

            player.resetTitle();

            Bukkit.getScheduler().callSyncMethod(playerDataManager.getCore(), () -> {
                player.removePotionEffect(PotionEffectType.DARKNESS);
                Bukkit.getPluginManager().callEvent(new PlayerLoadEvent(player));
                return null;
            });
        }));
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (loadingPlayers.containsKey(event.getPlayer().getUniqueId()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onInteract(AsyncChatEvent event) {
        if (loadingPlayers.containsKey(event.getPlayer().getUniqueId()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onInteract(EntityDamageEvent event) {
        if(event.getEntityType() == EntityType.PLAYER && loadingPlayers.containsKey(event.getEntity().getUniqueId()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if(loadingPlayers.containsKey(event.getPlayer().getUniqueId()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if(loadingPlayers.containsKey(event.getPlayer().getUniqueId()))
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
        ScheduledTask remove = loadingPlayers.remove(player.getUniqueId());
        if(remove != null) {
            remove.cancel();
        }else{
            playerDataManager.savePlayerData(player.getName(), player.getUniqueId(), playerDataManager.fetch(player));
        }
    }
}