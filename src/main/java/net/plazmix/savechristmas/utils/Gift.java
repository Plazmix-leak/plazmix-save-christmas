package net.plazmix.savechristmas.utils;

import com.google.common.collect.Multimap;
import net.plazmix.game.utility.GameSchedulers;
import net.plazmix.savechristmas.SaveChristmasGame;
import net.plazmix.utility.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Gift {




    private Location location;
    private Block block;
    private Date respawn;
    private int interval = SaveChristmasGame.getInstance().getCache().getInt(GameConstants.GIFT_SPAWNING_INTERVAL);
    private static HashMap<Location,Gift> giftSpawners = new HashMap<>();
    private static Class GameProfileClass;
    private static Class ProperyClass;
    private static Class BlockPositionClass;
    private static final String TEXTURES = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2M5YTg3MjI2ZmRhMDVhMWU2MjRlYmI3MmNmNzQwNjIzZTgxOTE4OWRmZWY1ODliNjgwNzdlNzVjMjQ4Y2U3OSJ9fX0=";
    private static Method setGameProfile;
    private static Method getWorldTileEntity;
    private static Method getWorldHandle;
    private static boolean initNms;

    public Gift(Location location, boolean isRespawning) {
        initNms();
        this.location = location;
        this.block = location.getWorld().getBlockAt(location);
        setGift();
        if(isRespawning) {
            this.respawn = new Date(-1L);
            giftSpawners.put(location, this);
            startUpdater();
        }
    }


    private void initNms() {
        if(!initNms) {
            try {
                GameProfileClass = Class.forName("com.mojang.authlib.GameProfile");
                //     GameProfileClass = Class.forName("net.minecraft.util.com.mojang.authlib.GameProfile");
                ProperyClass = Class.forName("com.mojang.authlib.properties.Property");
                BlockPositionClass = getNMSclass("BlockPosition");
                //     ProperyClass = Class.forName("net.minecraft.util.com.mojang.authlib.properties.Property");
                getWorldHandle = getCraftClass("CraftWorld").getMethod("getHandle");
                getWorldTileEntity = getNMSclass("WorldServer").getMethod("getTileEntity", BlockPositionClass);
                setGameProfile = getNMSclass("TileEntitySkull").getMethod("setGameProfile", GameProfileClass);
                initNms = true;
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }



    private void startUpdater() {
        GameSchedulers.runTimer(0, 20, new Runnable() {

            @Override
            public void run() {

                if(block.getType() == Material.AIR && new Date().after(respawn)) {
                    setGift();
                }

            }
        });
    }

    public void breakGift() {
        respawn = new Date(System.currentTimeMillis() + (long)interval * 1000L);
    }

    public static Gift getGiftSpawnerByLocation(Location location) {
        for(Location giftLocation: giftSpawners.keySet()) {
            if(giftLocation.distance(location) < 1) {
                return giftSpawners.get(giftLocation);
            }
        }

        return null;
    }

    private static Class<?> getNMSclass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
        String className = "net.minecraft.server." + version + name;
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return clazz;
    }

    private static Class<?> getCraftClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
        String className = "org.bukkit.craftbukkit." + version + name;
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return clazz;
    }

    private void setGift() {
        this.block.setType(Material.SKULL);
        setSkin((Skull) block.getState());
        this.block.getState().update(true);
    }

    private void setSkin(Skull skull) {
        Object gameProfile = null;
        Object property = null;
        String name = "Gift";

        try {
            gameProfile = GameProfileClass.getConstructor(UUID.class,String.class).newInstance(UUID.randomUUID(),name);
            Constructor propertyConstructor = ProperyClass.getConstructor(String.class,String.class);

            property = propertyConstructor.newInstance("textures",
                    Base64Coder.encodeString("{textures:{SKIN:{url:\"" + TEXTURES + "\"}}}"));
            Object propertiesMap = gameProfile.getClass().getMethod("getProperties")
                    .invoke(gameProfile);

            ((Multimap) propertiesMap).put("textures",property);

            Object world = getWorldHandle.invoke(skull.getWorld());
            Object blockPosition = BlockPositionClass.getConstructor(int.class,int.class,int.class)
                    .newInstance(skull.getX(),skull.getY(),skull.getZ());

            Object tileSkull = getWorldTileEntity.invoke(world, blockPosition);
            setGameProfile.invoke(tileSkull, gameProfile);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static ItemStack createItemGift() {
        return ItemUtil.newBuilder(Material.SKULL_ITEM).setName("§aПодарок")
               // .setUnbreakable(true)
              // .addItemFlag(ItemFlag.HIDE_UNBREAKABLE)
              // .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                .setDurability(3)
                .setTextureValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2M5YTg3MjI2ZmRhMDVhMWU2MjRlYmI3MmNmNzQwNjIzZTgxOTE4OWRmZWY1ODliNjgwNzdlNzVjMjQ4Y2U3OSJ9fX0=")
                .build();
    }
}
