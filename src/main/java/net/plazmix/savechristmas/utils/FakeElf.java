package net.plazmix.savechristmas.utils;

import net.plazmix.game.utility.GameSchedulers;
import net.plazmix.protocollib.entity.impl.FakeCreeper;
import net.plazmix.savechristmas.SaveChristmasGame;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

import java.util.Date;

public class FakeElf {

    private ElfSpawner elf;
    private Zombie fakeMob;

    public FakeElf(Location location,ElfSpawner spawner) {
        this.fakeMob = (Zombie) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
   //   if(fakeMob == null) {
   //       SaveChristmasGame.getInstance().getLogger().info("FAKEMOB NULL");
   //   }
   //   if(fakeMob.getLocation() == null) {
   //       SaveChristmasGame.getInstance().getLogger().info("FAKEMOB LOCATION NULL");
   //   }

   //   if(fakeMob.isOnGround()) {
   //       SaveChristmasGame.getInstance().getLogger().info("IS ON GROUND");
   //   }
    //    this.fakeMob.setCollidable(false);
    //   this.fakeMob.setInvulnerable(false);
        this.fakeMob.setSilent(true);
        this.elf = spawner;
    }

    public Location getLocation() {
        return fakeMob.getLocation();
    }

    public void setTarget(Player player) {
        fakeMob.setTarget(player);
    }

    public void remove() {
        fakeMob.remove();
    }

    public void teleport(Location location) {
        fakeMob.teleport(location);
    }

}
