package net.plazmix.savechristmas.utils;

import net.plazmix.game.user.GameUser;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class SPlayer {

    private static HashMap<UUID,SPlayer> sPlayers = new HashMap<>();
    private Player player;

    private SPlayer(Player player) {
        this.player = player;
        sPlayers.put(player.getUniqueId(),this);
    }


    public static SPlayer from(Player player) {
        return (sPlayers.get(player.getUniqueId()) == null) ? new SPlayer(player) : sPlayers.get(player.getUniqueId());
    }


    public ItemStack getGiftsInPlayerInventory() {
        for(ItemStack item: player.getInventory().getContents()) {
            if(item != null && item.getType() == Material.SKULL_ITEM) {
                return item;
            }
        }
        return null;
    }

    public void removeGift() {
        ItemStack gifts = getGiftsInPlayerInventory();
        gifts.setAmount(gifts.getAmount() - 1);
        GameUser.from(player).getCache().decrement(GameConstants.INGAME_PLAYER_GIFTS);
    }

    public void addGift() {
        ItemStack gifts = getGiftsInPlayerInventory();
        if (gifts == null)
        {
            player.getInventory().addItem(Gift.createItemGift());
        }
        else
        {
            gifts.setAmount(gifts.getAmount() + 1);
        }
        GameUser.from(player).getCache().increment(GameConstants.INGAME_PLAYER_GIFTS);
    }

}
