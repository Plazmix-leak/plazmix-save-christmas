package net.plazmix.savechristmas.items.shop;

import net.plazmix.game.user.GameUser;
import net.plazmix.inventory.button.action.impl.ClickableButtonAction;
import net.plazmix.inventory.impl.BaseSimpleInventory;
import net.plazmix.savechristmas.utils.GameConstants;
import net.plazmix.utility.ItemUtil;
import net.plazmix.utility.NumberUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class UpgradeInventory extends BaseSimpleInventory {

    public UpgradeInventory() {
        super(GameConstants.NAME_STORAGE_INVENTORY, 3);
    }


    @Override
    public void drawInventory(Player player) {

        ItemStack frameItem = ItemUtil.newBuilder(Material.STAINED_GLASS_PANE)
                .setDurability(15)
                .setName(ChatColor.RESET.toString())
                .build();

        for (int slot = 1; slot <= 13; slot++)
            setOriginalItem(slot, frameItem);

        GameUser user = GameUser.from(player);
        int storageGifts = user.getCache().get(GameConstants.INGAME_PLAYER_STORAGE_GIFTS);
        int level = storageGifts-1;
        int price = level*10;
        int balance = user.getCache().get(GameConstants.INGAME_PLAYER_BALANCE);
        boolean hasNextLevel = level < 3;
        boolean canAfford = balance >= price;

        setClickItem(14, getUpgradeItem(player), new ClickableButtonAction() {
            @Override
            public void buttonClick(Player player, InventoryClickEvent inventoryClickEvent) {
                if(!hasNextLevel) return;
                if(!canAfford) return;

                user.getCache().increment(GameConstants.INGAME_PLAYER_STORAGE_GIFTS);
                user.getCache().set(GameConstants.INGAME_PLAYER_BALANCE,balance-price);
                player.sendTitle("§aУспешно!",null,5,15,5);
                new UpgradeInventory().openInventory(player);
            }
        });

        for (int slot = 15; slot <= 27; slot++)
            setOriginalItem(slot, frameItem);


    }

    public ItemStack getUpgradeItem(Player player) {
        int storageGifts = GameUser.from(player).getCache().get(GameConstants.INGAME_PLAYER_STORAGE_GIFTS);
        int level = storageGifts-1;
        int nextLevel = level+1;
        int needMoney = level*10;
        int balance = GameUser.from(player).getCache().get(GameConstants.INGAME_PLAYER_BALANCE);
        boolean hasNextLevel = level < 3;

        ItemUtil.ItemBuilder upgradeItemBuilder = ItemUtil.newBuilder(Material.CHEST)
                .setName("§aУлучшение: Вместимость подарков")
                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                .addLore(" ")
                .addLore("§aВаш баланс§7: §e" +  NumberUtil.formattingSpaced(balance,"монета","монеты","монет"))
                .addLore("§aВаш уровень прокачки§7: §e" + String.valueOf(level))
                .addLore("§aВместимость подарков§7: §e" + storageGifts + (hasNextLevel ? "§7(+1)" : ""));
        if(hasNextLevel) {
            upgradeItemBuilder
                    .addLore(" ")
                    .addLore("§aСледующий уровень §7-> §e" + nextLevel)
                    .addLore("§aВместимость подарков§7: §e" + (storageGifts + 1))
                    .addLore("§aНеобходимо§7: §e" + NumberUtil.formattingSpaced(needMoney, "монета", "монеты", "монет"));

        }

        return upgradeItemBuilder.build();
    }
}
