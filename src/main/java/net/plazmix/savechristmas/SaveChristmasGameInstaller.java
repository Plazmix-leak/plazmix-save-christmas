package net.plazmix.savechristmas;

import net.plazmix.game.GamePlugin;
import net.plazmix.game.installer.GameInstallerTask;
import net.plazmix.savechristmas.entities.ExchangerNpcPlayer;
import net.plazmix.savechristmas.entities.ShopNpcPlayer;
import net.plazmix.savechristmas.utils.GameConstants;
import net.plazmix.savechristmas.utils.Panes;
import org.bukkit.*;
import org.bukkit.material.Wool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SaveChristmasGameInstaller extends GameInstallerTask {


    public SaveChristmasGameInstaller(GamePlugin plugin) {
        super(plugin);

    }

    @Override
    protected void handleExecute(Actions actions, Settings settings) {

        settings.setCenter(plugin.getService().getMapWorld().getSpawnLocation());
        settings.setRadius(50);
        settings.setUseOnlyTileBlocks(false);
  //      settings.setCuboidUse(false);

        plugin.getCache().set(GameConstants.ELF_SPAWNING_INTERVAL, 10);
        plugin.getCache().set(GameConstants.GIFT_SPAWNING_INTERVAL, 30);


        actions.addBlock(Material.YELLOW_FLOWER, block -> {
            block.setType(Material.AIR);

            List<Location> elfSpawnersLocations = plugin.getCache().getOrDefault(GameConstants.ELF_SPAWNS_CACHE, ArrayList::new);
            elfSpawnersLocations.add(block.getLocation().add(0,1,0));

            plugin.getCache().set(GameConstants.ELF_SPAWNS_CACHE, elfSpawnersLocations);
        });

        actions.addBlock(Material.WOOL, block -> {

            DyeColor woolColor = (new Wool(block.getType(), block.getData())).getColor();
            plugin.getService().getLoadedTeams().forEach(gameTeam -> {

                if(gameTeam.getChatColor() == getChatColorFromDyeColor(woolColor)) {

                    block.setType(Material.AIR);
                    List<Location> allowLocationsForPlaceGifts = gameTeam.getCache().getOrDefault(GameConstants.ALLOW_LOCATIONS_FOR_PLACE_GIFTS,ArrayList::new);
                    allowLocationsForPlaceGifts.add(block.getLocation());
                    gameTeam.getCache().set(GameConstants.ALLOW_LOCATIONS_FOR_PLACE_GIFTS,allowLocationsForPlaceGifts);

                }
            });
        });


        actions.addBlock(Material.STAINED_GLASS, block -> {

            ChatColor glassColor = getGlassChatColor(block.getData());
            plugin.getLogger().info("glass data " + block.getData());

            plugin.getService().getLoadedTeams().forEach(gameTeam -> {

                if(gameTeam.getChatColor() == glassColor) {

                    block.setType(Material.AIR);
                    List<Location> islandsSpawnsList = gameTeam.getCache().getOrDefault(GameConstants.ISLANDS_SPAWNS_CACHE, ArrayList::new);
                    islandsSpawnsList.add(block.getLocation());
                    gameTeam.getCache().set(GameConstants.ISLANDS_SPAWNS_CACHE, islandsSpawnsList);

                }
            });

        });


        actions.addBlock(Material.SIGN_POST, block -> {
            block.setType(Material.AIR);
            List<Location> giftSpawns = plugin.getCache().getOrDefault(GameConstants.GIFT_SPAWNS_CACHE,ArrayList::new);
            giftSpawns.add(block.getLocation());
            plugin.getCache().set(GameConstants.GIFT_SPAWNS_CACHE,giftSpawns);
        });

        actions.addBlock(Material.HOPPER, block -> {
            block.setType(Material.AIR);
            new ShopNpcPlayer(block.getLocation());
        });


        actions.addBlock(Material.BREWING_STAND, block -> {
            block.setType(Material.AIR);
            new ExchangerNpcPlayer(block.getLocation());
        });

    }

    private ChatColor getGlassChatColor(short glassData) {
        ChatColor color = null;
        for(Panes pane: Panes.values()) {
            if(pane.getData() == glassData) {
                color = ChatColor.valueOf(pane.name());
            }
        }
        return color;
    }


    private ChatColor getChatColorFromDyeColor(DyeColor color) {
        switch (color) {
            case YELLOW:
                return ChatColor.YELLOW;
            case BLUE:
                return ChatColor.BLUE;
            case RED:
                return ChatColor.RED;
            case GREEN:
                return ChatColor.GREEN;
        }
        return null;
    }
}
