package net.plazmix.savechristmas.states;

import net.plazmix.game.GamePlugin;
import net.plazmix.game.GamePluginService;
import net.plazmix.game.setting.GameSetting;
import net.plazmix.game.state.GameState;
import net.plazmix.game.team.GameTeam;
import net.plazmix.game.user.GameUser;
import net.plazmix.game.utility.GameSchedulers;
import net.plazmix.savechristmas.SaveChristmasGame;
import net.plazmix.savechristmas.scoreboards.IngameScoreboard;
import net.plazmix.savechristmas.utils.*;
import net.plazmix.utility.ItemUtil;
import net.plazmix.utility.PercentUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class IngameState extends GameState {

    private static GamePluginService service = SaveChristmasGame.getInstance().getService();
    private Date startDate;
    private Date endDate;
    private Date nextEvent;

    public IngameState(GamePlugin plugin) {
        super(plugin, "В игре", false);
    }


    @Override
    protected void onStart() {

        service.throwPlayersToTeams(4,service.getLoadedTeams());

        GameSetting.INTERACT_BLOCK.set(plugin.getService(), true);
        GameSetting.BLOCK_BREAK.set(plugin.getService(), false);
        GameSetting.BLOCK_PLACE.set(plugin.getService(), true);
        GameSetting.PLAYER_DAMAGE.set(plugin.getService(), true);
        GameSetting.ENTITY_DAMAGE.set(plugin.getService(), true);
        GameSetting.CREATURE_SPAWN_GENERIC.set(plugin.getService(), true);
        GameSetting.CREATURE_SPAWN_CUSTOM.set(plugin.getService(), true);

        startEndStateTimer();
        startElfDamageEvents();

        for(GameTeam team: service.getLoadedTeams()) {

            List<Location> islandLocationsList = team.getCache().get(GameConstants.ISLANDS_SPAWNS_CACHE);
            Location islandLocation = islandLocationsList.get((int) (Math.random() * islandLocationsList.size()));
            team.getCache().set(GameConstants.TEAM_GIFTS,0);

            for(GameUser user: team.getPlayerMap().values()) {

                Player player = user.getBukkitHandle();
                player.teleport(islandLocation);

                user.getCache().set(GameConstants.INGAME_PLAYER_ISLAND_LOC, islandLocation);
                user.getCache().set(GameConstants.INGAME_PLAYER_STORAGE_GIFTS, 2);
                user.getCache().set(GameConstants.INGAME_PLAYER_BALANCE, 0);
                user.getCache().set(GameConstants.INGAME_PLAYER_GIFTS, 0);
                islandLocationsList.remove(islandLocation);

                player.getInventory().clear();

                player.setFlying(false);
                player.setAllowFlight(false);

                player.setGameMode(GameMode.SURVIVAL);

                player.setLevel(0);
                player.setExp(0);
                player.getInventory().clear();

                user.getBukkitHandle().getInventory().setItem(0,createBlazeRodItem());

                player.getInventory()
                        .setHelmet(ItemUtil.newBuilder(Material.LEATHER_HELMET)
                                .setLeatherColor(getColorFromChatColor(user.getCurrentTeam().getChatColor()))
                                .setUnbreakable(true)
                                .build());

                player.getInventory()
                        .setChestplate(ItemUtil.newBuilder(Material.LEATHER_CHESTPLATE)
                                .setLeatherColor(getColorFromChatColor(user.getCurrentTeam().getChatColor()))
                                .setUnbreakable(true)
                                .build());

                player.getInventory()
                        .setLeggings(ItemUtil.newBuilder(Material.LEATHER_LEGGINGS)
                                .setLeatherColor(getColorFromChatColor(user.getCurrentTeam().getChatColor()))
                                .setUnbreakable(true)
                                .build());

                player.getInventory()
                        .setBoots(ItemUtil.newBuilder(Material.LEATHER_BOOTS)
                                .setLeatherColor(getColorFromChatColor(user.getCurrentTeam().getChatColor()))
                                .setUnbreakable(true)
                                .build());
                new IngameScoreboard(user.getBukkitHandle(),endDate);
            }

        }

        ((List<Location>) plugin.getCache().get(GameConstants.ELF_SPAWNS_CACHE)).forEach(spawnLocation -> {
            new ElfSpawner(spawnLocation);
        });

        List<Location> iteratorGiftsLocations = (List<Location>) plugin.getCache().get(GameConstants.GIFT_SPAWNS_CACHE);
        List<Location> giftsLocations = new ArrayList<>(iteratorGiftsLocations);

        for(Location giftLocation: iteratorGiftsLocations) {
            if(giftsLocations.size() <= 9) break;
            new Gift(giftLocation,true);
            giftsLocations.remove(giftLocation);
        }
        plugin.getCache().set(GameConstants.GIFT_SPAWNS_CACHE,giftsLocations);

    }


    private void startElfDamageEvents() {
        nextEvent = new Date(System.currentTimeMillis() + 1000L * 60);
        GameSchedulers.runTimer(0, 20, new Runnable() {
            @Override
            public void run() {

                if(new Date().after(nextEvent)) {
                    ElfSpawner.getSpawners().forEach(elf -> {
                        elf.incrementDamage(0.5);
                    });

                    List<Location> giftLocations = (List<Location>) plugin.getCache().get(GameConstants.GIFT_SPAWNS_CACHE);
                    Location giftLocation = giftLocations.get(0);
                    giftLocations.remove(giftLocation);
                    new Gift(giftLocation,true);
                    plugin.getCache().set(GameConstants.GIFT_SPAWNS_CACHE,giftLocations);

                    nextEvent = new Date(System.currentTimeMillis() + 1000L * 60);
                }
            }
        });
    }

    private void startEndStateTimer() {
        startDate = new Date();
        endDate = new Date(System.currentTimeMillis() + 1000L * 10 * 60);

        new BukkitRunnable() {
            @Override
            public void run() {
                if(startDate.after(endDate)) {
                   // plugin.getCache().set(GameConstants.WINNER_TEAM,getMaxGiftsTeam());
                    SaveChristmasGame.getInstance().getLogger().info("nextStage!!!!!");
                    nextStage();
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin,0,20);

    }

    @Override
    protected void onShutdown() {

    }

    private Color getColorFromChatColor(ChatColor chatColor) {
        switch(chatColor) {
            case RED:
                return Color.RED;
            case GREEN:
                return Color.GREEN;
            case BLUE:
                return Color.BLUE;
            case YELLOW:
                return Color.YELLOW;
            default:
                return null;
        }
    }

    private GameTeam getMaxGiftsTeam() {

        int maxGifts = Integer.MAX_VALUE;
        GameTeam winnerTeam = null;

        for(GameTeam team: plugin.getService().getLoadedTeams()) {

            int gifts = team.getCache().get(GameConstants.TEAM_GIFTS);
            if(maxGifts == Integer.MAX_VALUE || gifts > maxGifts) {
                maxGifts = gifts;
                winnerTeam = team;
            }
        }

        return winnerTeam;
    }


    //For blaze rod

    private ItemStack createBlazeRodItem() {
        return ItemUtil.newBuilder(Material.BLAZE_ROD).setUnbreakable(true)
                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                .addItemFlag(ItemFlag.HIDE_UNBREAKABLE)
                .addItemFlag(ItemFlag.HIDE_ENCHANTS)
                .setName(GameConstants.BLAZERODNAME)
                .addEnchantment(Enchantment.DAMAGE_ALL,1)
                .build();
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if(isBlazeRod(event.getItemDrop().getItemStack())) event.setCancelled(true);
    }

    @EventHandler
    public void onMove(InventoryClickEvent event) {
        if(isBlazeRod(event.getCurrentItem())) event.setCancelled(true);
    }

    public boolean isBlazeRod(ItemStack item) {
        if(item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return false;

        if(item.getType() == Material.BLAZE_ROD && item.getItemMeta().getDisplayName().equalsIgnoreCase(GameConstants.BLAZERODNAME)) {
            return true;
        }

        return false;
    }

    @EventHandler
    public void onGetGift(PlayerInteractEvent event) {

        Block clickedBlock = event.getClickedBlock();
        if(clickedBlock == null || clickedBlock.getType() == null) return;
        if(event.getHand() != EquipmentSlot.HAND) return;


        if(clickedBlock.getType() == Material.SKULL) {

            Player p = event.getPlayer();
            GameUser user = GameUser.from(p);
            int storage = user.getCache().get(GameConstants.INGAME_PLAYER_STORAGE_GIFTS);
            int gifts = user.getCache().get(GameConstants.INGAME_PLAYER_GIFTS);
            if(gifts >= storage) {
                p.sendMessage(GameConstants.PREFIX + "§cВаше хранилище подарков заполнено, вы можете улучшить его в магазине!");
                return;
            }
            if(p.getInventory().firstEmpty() == -1) {
                p.sendMessage(GameConstants.PREFIX + "§cВаш инвентарь заполнен!");
                return;
            }
            clickedBlock.setType(Material.AIR);
            SPlayer.from(p).addGift();
            p.sendMessage(GameConstants.PREFIX + "§aВы взяли подарок, отнесите его под елку!");
            int playerGifts = user.getCache().get(GameConstants.INGAME_PLAYER_GIFTS);
            int playerGiftsInHand = getAmountGiftsInInventory(p);

            try {
                Gift.getGiftSpawnerByLocation(clickedBlock.getLocation()).breakGift();
            } catch (Exception e) {

            }

            if(playerGiftsInHand < playerGifts) {
                user.getCache().decrement(GameConstants.INGAME_PLAYER_GIFTS);
            }
        }

    }

    private int getAmountGiftsInInventory(Player player) {
        int amount = 0;
        for(ItemStack item: player.getInventory().getContents()) {
            if(item != null && item.getType() == Material.SKULL_ITEM) {
                amount = item.getAmount();
            }
        }
        return amount;
    }


    @EventHandler
    public void onDead(EntityDamageEvent event) {
        if(event.getEntity().getType() == EntityType.ZOMBIE) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onAddGiftToCommand(BlockPlaceEvent event) {
        if(event.isCancelled()) event.setCancelled(false);

        ItemStack hand = event.getPlayer().getInventory().getItemInHand();
        if(hand == null || hand.getType() != Material.SKULL_ITEM) {
            return;
        }

        Location location = event.getBlock().getLocation();
        GameUser user = GameUser.from(event.getPlayer());
        GameTeam playerTeam = user.getCurrentTeam();
        boolean isLocationForPlaceGift = isGiftLocation(playerTeam,location);


        if(isLocationForPlaceGift) {

            if(event.isCancelled()) event.setCancelled(false);
            playerTeam.getCache().increment(GameConstants.TEAM_GIFTS);
            user.getCache().decrement(GameConstants.INGAME_PLAYER_GIFTS);
            hand.setAmount(hand.getAmount() - 1);
            Bukkit.broadcastMessage(GameConstants.PREFIX + "§aИгрок §e" + user.getName() + " §aпринес своей команде подарок!");

        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onPvp(EntityDamageByEntityEvent event) {
        if(!(event.getDamager() instanceof Player)) return;
        if(!(event.getEntity() instanceof Player)) return;

        GameUser attacker = GameUser.from((Player) event.getDamager());
        GameUser player = GameUser.from((Player) event.getEntity());

        if(player.getCurrentTeam() == attacker.getCurrentTeam()) event.setCancelled(true);


    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
        event.setKeepInventory(true);
        GameUser user = GameUser.from(event.getEntity());
        event.getEntity().teleport((Location) user.getCache().get(GameConstants.INGAME_PLAYER_ISLAND_LOC));
    }


    private boolean isGiftLocation(GameTeam team, Location location) {
       for(Location giftLocation: (ArrayList<Location>)team.getCache().get(GameConstants.ALLOW_LOCATIONS_FOR_PLACE_GIFTS)) {
           if (location.distance(giftLocation) < 1) return true;
       }
      //  if(((ArrayList<Location>) team.getCache().get(GameConstants.ALLOW_LOCATIONS_FOR_PLACE_GIFTS)).contains(location)) {
      //      return true;
      //  }
       return false;
    }

    private boolean isElfSpawnLocation(Location location) {
        for(Location elfLocation: (ArrayList<Location>)plugin.getCache().get(GameConstants.ELF_SPAWNS_CACHE)) {

            if (location.getX() == elfLocation.getX() && location.getY() == elfLocation.getY() && location.getZ() == elfLocation.getZ()){
                return true;
            }
        }
        return false;
    }


    //Для FakeElf
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Zombie) event.setCancelled(true);
        if(event.getEntity() instanceof Zombie) event.setCancelled(true);
    }



    @EventHandler
    public void onSpawn(EntitySpawnEvent event) {
        if(event.getEntityType() == EntityType.ZOMBIE && isElfSpawnLocation(event.getLocation()))
        {
            if(event.isCancelled()) event.setCancelled(false);
        }
        else
        {
            event.setCancelled(true);
        }

    }
}

