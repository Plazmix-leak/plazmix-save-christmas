package net.plazmix.savechristmas.items.shop;

import net.plazmix.game.user.GameUser;
import net.plazmix.inventory.button.action.impl.ClickableButtonAction;
import net.plazmix.inventory.impl.BaseSimpleInventory;
import net.plazmix.savechristmas.utils.GameConstants;
import net.plazmix.savechristmas.utils.SPlayer;
import net.plazmix.utility.ItemUtil;
import net.plazmix.utility.NumberUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class ExchangeInventory extends BaseSimpleInventory {


    public ExchangeInventory() {
        super(GameConstants.EXCHANGE_INVENTORY, 3);
    }


    @Override
    public void drawInventory(Player player) {

        ItemStack frameItem = ItemUtil.newBuilder(Material.STAINED_GLASS_PANE)
                .setDurability(15)
                .setName(ChatColor.RESET.toString())
                .build();

        for (int slot = 1; slot <= 13; slot++)
            setOriginalItem(slot, frameItem);

        ItemStack giftsItem = SPlayer.from(player).getGiftsInPlayerInventory();
        int giftsAmount = (giftsItem == null) ? 0 : giftsItem.getAmount();

        setClickItem(14, getUpgradeItem(player), new ClickableButtonAction() {
            @Override
            public void buttonClick(Player player, InventoryClickEvent inventoryClickEvent) {
                if(giftsAmount <= 0) {
                    player.sendMessage(GameConstants.PREFIX + "§cУ вас недостаточно подарков!");
                    return;
                }
                SPlayer.from(player).removeGift();
                int balance = GameUser.from(player).getCache().get(GameConstants.INGAME_PLAYER_BALANCE);
                GameUser.from(player).getCache().set(GameConstants.INGAME_PLAYER_BALANCE,balance+10);
                player.sendMessage(GameConstants.PREFIX + "§aВы успешно обменяли §c1 подарок на §e10 монет!");
                new ExchangeInventory().openInventory(player);
            }
        });

        for (int slot = 15; slot <= 27; slot++)
            setOriginalItem(slot, frameItem);


    }

    public ItemStack getUpgradeItem(Player player) {
        ItemStack giftsItem = SPlayer.from(player).getGiftsInPlayerInventory();
        int giftsAmount = (giftsItem == null) ? 0 : giftsItem.getAmount();

        ItemUtil.ItemBuilder upgradeItemBuilder = ItemUtil.newBuilder(Material.CHEST)
                .setName("§aОбменник подарков на монеты")
                .addLore(" ")
                .addLore("§c1 подарок §a- §e10 монет")
                .addLore(" ")
                .addLore("§aВаши подарки§6: " + giftsAmount)
                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES);
        if(giftsAmount > 0) {
            upgradeItemBuilder
                    .addLore(" ")
                    .addLore("§eКликните, чтобы обменять §cподарок §aна §e10 монет§a!");

        }

        return upgradeItemBuilder.build();
    }


}
