package net.plazmix.savechristmas.states;

import net.plazmix.core.PlazmixCoreApi;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.state.type.StandardEndingState;
import net.plazmix.game.team.GameTeam;
import net.plazmix.game.utility.hotbar.GameHotbar;
import net.plazmix.game.utility.hotbar.GameHotbarBuilder;
import net.plazmix.savechristmas.scoreboards.EndingScoreboard;
import net.plazmix.savechristmas.utils.GameConstants;
import net.plazmix.utility.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

public class EndingState extends StandardEndingState {

    private final GameHotbar gameHotbar = GameHotbarBuilder.newBuilder()
            .setMoveItems(true)

            .addItem(5, ItemUtil.newBuilder(Material.PAPER)
                            .setName("§aСыграть еще раз")
                            .build(),

                    player -> GamePlugin.getInstance().getService().playAgain(player))

            .addItem(9, ItemUtil.newBuilder(Material.SKULL_ITEM)
                            .setName("§aПокинуть арену §7(ПКМ)")
                            .build(),

                    player -> PlazmixCoreApi.redirect(player, "hub"))
            .build();

    public EndingState(GamePlugin plugin) {
        super(plugin, "Конец");
    }


    @Override
    protected String getWinnerPlayerName() {
        return null;
    }

    @Override
    protected void handleStart() {
        Bukkit.getOnlinePlayers().forEach(gameHotbar::setHotbarTo);
        GameTeam team = plugin.getCache().get(GameConstants.WINNER_TEAM);
        team.getPlayers().forEach(user -> {
            ///...
        });
    }

    @Override
    protected void handleScoreboardSet(Player player) {
        new EndingScoreboard(plugin.getCache().get(GameConstants.WINNER_TEAM),player);
    }

    @Override
    protected Location getTeleportLocation() {
        return null;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }
}

