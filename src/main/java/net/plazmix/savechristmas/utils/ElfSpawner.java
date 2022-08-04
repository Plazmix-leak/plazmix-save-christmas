package net.plazmix.savechristmas.utils;

import net.plazmix.game.GameCache;
import net.plazmix.game.user.GameUser;
import net.plazmix.game.utility.GameSchedulers;
import net.plazmix.protocollib.entity.animation.FakeEntityAnimation;
import net.plazmix.protocollib.entity.impl.FakePlayer;
import net.plazmix.savechristmas.SaveChristmasGame;
import net.plazmix.utility.NumberUtil;
import net.plazmix.utility.mojang.MojangUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ElfSpawner {

    private final Location spawnLocation;
    private FakePlayer elf;
    private Date respawn;
    private int interval = SaveChristmasGame.getInstance().getCache().get(GameConstants.ELF_SPAWNING_INTERVAL);
    private int hp;
    private final int maxXp = 30;
    private BukkitTask targetTask;
    private BukkitTask moveTask;
    private final int attackRadius = 10;
    private boolean dead;
    private static String SIGNATURE = "WogjrB5x4o/fLDQUjUOKhl+5NJc5tczqx3fX1uA2z0gm+nVQt0QsAhpKdSlVlWlS6KyuMMfCX6Zwk+cC5vxh53Zb0fyjG+fDteNkAMVD4P/J8rOwrwWtAJzf7LlaLE2ZDHvcaaZuHt2fi0ghiklospSr0hFFInV7XmqJMEEdF+cJzD3FC3nKg4C63JybWkfGUwyA998QiEISKMoHSCmae6YOXuqWQzmlSQSDIbXGRVXgwQ4IwDqt5wRP9ohHErtauod39En3jjVcoDIZ+d8GrrvlFHzX8fpP6HlekGvsVXOdoS/kPNUDZmXb86+afWaoj5bMzHj0v0m0Q9nurnXuspPnmf+RuwdhRWKRzBbkRrrqoWFqmrVZf/+mrPZsMRqY7i1Oukjqa8iLt8h2FJzclVgOFZrX0wTRpwhkKRwrLtG4jq+STNvejXzuHcLT99bYEghtJ/cLi2mKQzGIVs/ss87S5GOpHcFVi5mg5yUQG3Vd9r/esThLsLxwSQigcXsBDKNCu6qm/Xchmu9m0N60m6Zvulz65c4DsTrxR8Q3srMxVDgcjdhv4r7pLkLib/i4nOJFAuO30N8k0wAnGsHFpHHrULSbZti4ubuRuxI/Qhm3dWOK0Jh+KmhohjrUlzmz63HP6cDjWxHa8V5/0RBwMiomivVMfNJsmk97dltF1Ug=";
    private static String VALUE = "ewogICJ0aW1lc3RhbXAiIDogMTYwNjkzMDc0NDU0NiwKICAicHJvZmlsZUlkIiA6ICJmNWQwYjFhZTQxNmU0YTE5ODEyMTRmZGQzMWU3MzA1YiIsCiAgInByb2ZpbGVOYW1lIiA6ICJDYXRjaFRoZVdhdmUxMCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8xMmIxZWRlY2E0NDU0ZmI1ZTcyOGUzYWI5MzcwNmQ5N2ZhODVjMmM2YzczMjg5MTU4NGQ4MDc4NTc0NTRiMjU4IgogICAgfQogIH0KfQ==";
    private FakeElf fakeElf;
    private double elfDamage;
    private static List<ElfSpawner> spawners = new ArrayList<>();

    public ElfSpawner(Location location) {
        this.spawnLocation = location.clone();
        this.hp = maxXp;
        this.elf = null;
        this.respawn = new Date(-1L);
        this.dead = true;
        this.elfDamage = 1.5;
        startUpdater();
        spawners.add(this);
    }

    public void incrementDamage(double amount) {
        this.elfDamage+=amount;
    }

    public Location getSpawnLocation() {
        return spawnLocation.clone();
    }

    public static List<ElfSpawner> getSpawners() {
        return spawners;
    }

    public void startUpdater() {
        GameSchedulers.runTimer(0, 20, new Runnable() {

            @Override
            public void run() {

           //     elf.teleport(fakeElf.getLocation());
                if(!dead && elf == null) {
                    dead(null,null);
                }

                if(dead && new Date().after(respawn)) {
                    spawnElf();
                }

                if(!dead && (targetTask == null || targetTask.isCancelled())) {
                    startTargetUpdater();
                }

                if(!dead && spawnLocation.distance(elf.getLocation()) > 15) {
                    elf.teleport(spawnLocation);
                    fakeElf.teleport(spawnLocation);
                }

            }
        });
    }


    public void dead(Player killer, Location deadLocation) {

   //     elf.playAnimationAll(FakeEntityAnimation.TAKE_DAMAGE);

        GameUser user = GameUser.from(killer);

        if(user != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    deadLocation.getWorld().playSound(deadLocation,Sound.ENTITY_FIREWORK_SHOOT,2,1);
                    new Gift(deadLocation,false);

                }
            }.runTaskLater(SaveChristmasGame.getInstance(),20L);

        //   int money = PercentUtil.acceptRandomPercent(50) ? 2 : 3;
        //   if (PercentUtil.acceptRandomPercent(50)) {

        //       addMoneyForKillElf(user, money);

        //   } else {

        //       if (storageIsFull(user)) {
        //           addMoneyForKillElf(user, money);
        //           return;
        //       }
        //       addGiftForKillElf(user);

        //   }
        }

        this.dead = true;
        if(!targetTask.isCancelled()) targetTask.cancel();
        this.elf.remove();
        this.elf = null;
        this.fakeElf.remove();
        this.fakeElf = null;
        this.respawn = new Date(System.currentTimeMillis() + (long)interval * 1000L);

    }

    private void addMoneyForKillElf(GameUser user, int money) {
        user.getCache().set(GameConstants.INGAME_PLAYER_BALANCE,
                user.getCache().getInt(GameConstants.INGAME_PLAYER_BALANCE) + money);
        user.getBukkitHandle().sendMessage(GameConstants.PREFIX + "§aВы получили §e" + NumberUtil.formattingSpaced(money,"монета","монеты","монет")
                + " §aза убийство злого эльфа!");
    }

    private void addGiftForKillElf(GameUser user) {
        user.getBukkitHandle().getInventory().addItem(Gift.createItemGift());
        user.getCache().increment(GameConstants.INGAME_PLAYER_GIFTS);
        user.getBukkitHandle().sendMessage(GameConstants.PREFIX + "§aВы получили §eподарок §aза убийство злого эльфа!");
    }

    private boolean storageIsFull(GameUser user) {
        GameCache cache = user.getCache();
        if(cache.getInt(GameConstants.INGAME_PLAYER_GIFTS) >= cache.getInt(GameConstants.INGAME_PLAYER_STORAGE_GIFTS)) {
            return true;
        }
        return false;
    }

    public void spawnElf() {
        try {

            this.elf = new FakePlayer(MojangUtil.createSkinObject(VALUE,SIGNATURE), spawnLocation);
            this.hp = maxXp;
            this.elf.setCustomName("Эльф");
            this.elf.setCustomNameVisible(true);
            this.fakeElf = new FakeElf(spawnLocation,this);
            elf.setAttackAction(player -> {

                for (GameUser alivePlayer : SaveChristmasGame.getInstance().getService().getAlivePlayers()) {

                    elf.playAnimation(FakeEntityAnimation.TAKE_DAMAGE, alivePlayer.getBukkitHandle());
                    elf.look(player.getLocation());

                }

                ItemStack hand = player.getInventory().getItemInHand();

                short durability = (hand == null) ? 1 : hand.getDurability();
                if (durability < 0) durability = 1;
                if (hand != null && hand.getType() == Material.BLAZE_ROD) durability = 3;

                this.hp = hp - durability;

                if (this.hp <= 0) {
                    dead(player,elf.getLocation());
                    return;
                }

                player.playSound(player.getLocation(), Sound.ENTITY_SKELETON_HURT, 2, 1);
            });
            this.elf.spawn();
            this.dead = false;
            startTargetUpdater();

        } catch (NullPointerException e) {

           dead(null,null);
           targetTask.cancel();

        }
  //    SaveChristmasGame.getInstance().getLogger().info("spawn elf, location ");
  //    SaveChristmasGame.getInstance().getLogger().info("X " + location.getX());
  //    SaveChristmasGame.getInstance().getLogger().info("Y " + location.getY());
  //    SaveChristmasGame.getInstance().getLogger().info("Z " + location.getZ());
    }


    public boolean isDead() {
        return dead;
    }

    private void startTargetUpdater() {

        targetTask = new BukkitRunnable() {

            @Override
            public void run() {
                Player player = getNearestPlayer(elf);
              //  SaveChristmasGame.getInstance().getLogger().info("Nearest player " + player.get());
                if(player == null) return;

                double distance = elf.getLocation().distance(player.getLocation());
                boolean isNear = distance <= 3;

                if(isNear){
                    fakeElf.setTarget(null);
                    fakeElf.teleport(elf.getLocation());
                    attack(player);
                    this.cancel();
                    moveTask.cancel();
                }

                if(distance < attackRadius) {
                    elf.look(player);
                    fakeElf.setTarget(player);
                    startMoveTask();
                }



            }

        }.runTaskTimer(SaveChristmasGame.getInstance(),0,10);

    }

    public void startMoveTask() {
        fakeElf.teleport(elf.getLocation());
        moveTask = new BukkitRunnable() {

            @Override
            public void run() {
                if(dead) this.cancel();
                elf.teleport(fakeElf.getLocation());
            }
        }.runTaskTimer(SaveChristmasGame.getInstance(),0,3L);
    }

    private Player getNearestPlayer(FakePlayer elf) {
        double closestDistance = Double.MAX_VALUE;
        Player closestPlayer = null;
        for(GameUser alivePlayer : SaveChristmasGame.getInstance().getService().getAlivePlayers()) {

            Player aliveBukkitPlayer = alivePlayer.getBukkitHandle();
            double dist = aliveBukkitPlayer.getLocation().distance(elf.getLocation());
            if (closestDistance == Double.MAX_VALUE || dist < closestDistance) {
                closestDistance = dist;
                closestPlayer = aliveBukkitPlayer;
            }

        }

        return closestPlayer;
    }

    private void attack(Player player) {
        if(dead) return;
        for(GameUser alivePlayer : SaveChristmasGame.getInstance().getService().getAlivePlayers()) {

            Player aliveBukkitPlayer = alivePlayer.getBukkitHandle();
            elf.playAnimation(FakeEntityAnimation.SWING_MAIN_HAND,aliveBukkitPlayer);
            player.damage(elfDamage);

        }
    }

}
