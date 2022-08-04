package net.plazmix.savechristmas.scoreboards;

import net.md_5.bungee.api.ChatColor;
import net.plazmix.core.PlazmixCoreApi;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.state.type.StandardWaitingState;
import net.plazmix.game.team.GameTeam;
import net.plazmix.game.user.GameUser;
import net.plazmix.savechristmas.utils.GameConstants;
import net.plazmix.scoreboard.BaseScoreboardBuilder;
import net.plazmix.scoreboard.BaseScoreboardScope;
import net.plazmix.utility.DateUtil;
import net.plazmix.utility.NumberUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Date;

public class IngameScoreboard {

    private Date endDate;

    public IngameScoreboard(Player player, Date endDate) {
        this.endDate = endDate;
        GamePlugin gamePlugin = GamePlugin.getInstance();
        BaseScoreboardBuilder scoreboardBuilder = BaseScoreboardBuilder.newScoreboardBuilder();
        scoreboardBuilder.scoreboardDisplay("§с§lСПАСЕНИЕ РОЖДЕСТВА");
        scoreboardBuilder.scoreboardScope(BaseScoreboardScope.PROTOTYPE);

        scoreboardBuilder.scoreboardLine(10, "До конца игры: §c" + getCorrectTimer());
        scoreboardBuilder.scoreboardLine(9, "");

        int i = 8;
        for(GameTeam team: gamePlugin.getService().getLoadedTeams()) {
            Object gifts = team.getCache().get(GameConstants.TEAM_GIFTS);

            if(gifts == null) {
                team.getCache().set(GameConstants.TEAM_GIFTS,0);
                gifts = team.getCache().get(GameConstants.TEAM_GIFTS);
            }


            scoreboardBuilder.scoreboardLine(i,team.getChatColor() + team.getTeamName() + ": " + NumberUtil.formattingSpaced((int)gifts, "§fподарок", "§fподарка", "§fподарков"));
            i--;
        }

        scoreboardBuilder.scoreboardLine(4, "§fВаши монеты: §e" + GameUser.from(player).getCache().get(GameConstants.INGAME_PLAYER_BALANCE));
        scoreboardBuilder.scoreboardLine(3, "§fВаши подарки: §a" + GameUser.from(player).getCache().get(GameConstants.INGAME_PLAYER_GIFTS) + "/§c" + GameUser.from(player).getCache().get(GameConstants.INGAME_PLAYER_STORAGE_GIFTS));
        scoreboardBuilder.scoreboardLine(2, "");
        scoreboardBuilder.scoreboardLine(1, "§dwww.plazmix.net");

        scoreboardBuilder.scoreboardUpdater((baseScoreboard, player1) -> {
            scoreboardBuilder.scoreboardLine(10, "До конца игры: §c" + getCorrectTimer());
            int i1 = 8;
            for(GameTeam team: gamePlugin.getService().getLoadedTeams()) {
                int gifts = team.getCache().get(GameConstants.TEAM_GIFTS);

                scoreboardBuilder.scoreboardLine(i1,team.getChatColor() + team.getTeamName() + ": " + NumberUtil.formattingSpaced((int)gifts, "§fподарок", "§fподарка", "§fподарков"));
                i1--;
            }

            scoreboardBuilder.scoreboardLine(4, "§fВаши монеты: §e" + GameUser.from(player).getCache().get(GameConstants.INGAME_PLAYER_BALANCE));
            scoreboardBuilder.scoreboardLine(3, "§fВаши подарки: §a" + GameUser.from(player).getCache().get(GameConstants.INGAME_PLAYER_GIFTS) + "/§c" + GameUser.from(player).getCache().get(GameConstants.INGAME_PLAYER_STORAGE_GIFTS));
            scoreboardBuilder.build().setScoreboardToPlayer(player);
        }, 20);

        scoreboardBuilder.build().setScoreboardToPlayer(player);
    }

    private String getCorrectTimer() {
        long totalSecs = (endDate.getTime() - new Date().getTime())/1000;
        int minutes = (int) ((totalSecs % 3600) / 60);
        int seconds = (int) (totalSecs % 60);

        String timeString = String.format("%02d:%02d", minutes, seconds);
        return timeString;
    }
}
