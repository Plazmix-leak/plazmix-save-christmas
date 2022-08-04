package net.plazmix.savechristmas.states;

import net.plazmix.core.PlazmixCoreApi;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.setting.GameSetting;
import net.plazmix.game.state.type.StandardWaitingState;
import net.plazmix.game.utility.GameSchedulers;
import net.plazmix.game.utility.hotbar.GameHotbar;
import net.plazmix.game.utility.hotbar.GameHotbarBuilder;
import net.plazmix.savechristmas.scoreboards.WaitingScoreboard;
import net.plazmix.savechristmas.utils.GameConstants;
import net.plazmix.utility.ItemUtil;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class WaitingState extends StandardWaitingState {

    private final GameHotbar gameHotbar = GameHotbarBuilder.newBuilder()
            .setMoveItems(false)
            .addItem(9, ItemUtil.newBuilder(Material.REDSTONE_BLOCK)
                            .setName("§aПокинуть арену")
                            .build(),
                    PlazmixCoreApi::redirectToLobby)
            .build();


    public WaitingState(GamePlugin plugin) {
        super(plugin, "Ожидание игроков");

        GameSetting.INTERACT_BLOCK.set(plugin.getService(), false);
        GameSetting.FOOD_CHANGE.set(plugin.getService(), false);
        GameSetting.PLAYER_DAMAGE.set(plugin.getService(), false);
        GameSetting.BLOCK_BREAK.set(plugin.getService(), false);
        GameSetting.BLOCK_PLACE.set(plugin.getService(), false);
        GameSetting.ENTITY_DAMAGE.set(plugin.getService(), false);
    }

    @Override
    protected Location getTeleportLocation() {
        return plugin.getService().getMapWorld().getSpawnLocation().clone().add(0.5, 0, 0.5);
    }

    @Override
    protected void handleEvent(PlayerJoinEvent event) {
        int online = Bukkit.getOnlinePlayers().size();
        int maxOnline = getPlugin().getService().getMaxPlayers();

        GameSchedulers.runLater(10, () -> {
            new WaitingScoreboard(getTimerStatus(), event.getPlayer());
            gameHotbar.setHotbarTo(event.getPlayer());
        });


        event.setJoinMessage(GameConstants.PREFIX + PlazmixUser.of(event.getPlayer()).getDisplayName() + " §fподключился к игре! §7(" + online + "/" + maxOnline + ")");

        if (online >= maxOnline && !timerStatus.isLived()) {
            timerStatus.runTask();
        }

    }

    @Override
    protected void handleEvent (PlayerQuitEvent event) {
        int online = Bukkit.getOnlinePlayers().size() - 1;
        int maxOnline = getPlugin().getService().getMaxPlayers();

        event.setQuitMessage(GameConstants.PREFIX + PlazmixUser.of(event.getPlayer()).getDisplayName() + " §fпокинул игру! §7(" + online + "/" + maxOnline + ")");

        if (online < maxOnline && timerStatus.isLived()) {
            timerStatus.cancelTask();
        }

    }

    @Override
    protected void handleTimerUpdate(TimerStatus timerStatus) {

    }
}
