package net.plazmix.savechristmas;

import net.plazmix.game.GamePlugin;
import net.plazmix.game.installer.GameInstaller;
import net.plazmix.game.installer.GameInstallerTask;
import net.plazmix.game.setting.GameSetting;
import net.plazmix.game.team.GameTeam;
import net.plazmix.savechristmas.states.EndingState;
import net.plazmix.savechristmas.states.IngameState;
import net.plazmix.savechristmas.states.WaitingState;
import org.bukkit.ChatColor;

/*  Leaked by https://t.me/leak_mine
    - Все слитые материалы вы используете на свой страх и риск.

    - Мы настоятельно рекомендуем проверять код плагинов на хаки!
    - Список софта для декопиляции плагинов:
    1. Luyten (последнюю версию можно скачать можно тут https://github.com/deathmarine/Luyten/releases);
    2. Bytecode-Viewer (последнюю версию можно скачать можно тут https://github.com/Konloch/bytecode-viewer/releases);
    3. Онлайн декомпиляторы https://jdec.app или http://www.javadecompilers.com/

    - Предложить свой слив вы можете по ссылке @leakmine_send_bot или https://t.me/leakmine_send_bot
*/

public final class SaveChristmasGame extends GamePlugin {

    @Override
    public GameInstallerTask getInstallerTask() {
        return new SaveChristmasGameInstaller(this);
    }

    @Override
    protected void handleEnable() {
        saveDefaultConfig();
        service.setMapName(getConfig().getString("map", getServer().getWorlds().get(0).getName()));

        service.setServerMode("savechristmas");
        service.setGameName("Save Christmas");


        service.registerState(new WaitingState(this));
        service.registerState(new IngameState(this));
        service.registerState(new EndingState(this));

        service.setSetting(GameSetting.FOOD_CHANGE,false);

        service.registerTeam(new GameTeam(1, ChatColor.YELLOW,"Желтые"));
        service.registerTeam(new GameTeam(2, ChatColor.GREEN,"Зеленые"));
        service.registerTeam(new GameTeam(3, ChatColor.RED,"Красные"));
        service.registerTeam(new GameTeam(4, ChatColor.BLUE,"Синие"));

        service.setMaxPlayers(16);

        GameInstaller.create().executeInstall(this.getInstallerTask());
    }

    @Override
    protected void handleDisable() {

    }

}
