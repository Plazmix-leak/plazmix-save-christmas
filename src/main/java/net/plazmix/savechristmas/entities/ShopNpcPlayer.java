package net.plazmix.savechristmas.entities;

import net.plazmix.protocollib.entity.impl.FakePlayer;
import net.plazmix.savechristmas.SaveChristmasGame;
import net.plazmix.savechristmas.items.shop.UpgradeInventory;
import net.plazmix.savechristmas.utils.GameConstants;
import net.plazmix.utility.mojang.MojangSkin;
import net.plazmix.utility.mojang.MojangUtil;
import org.bukkit.Location;

public class ShopNpcPlayer {

    private FakePlayer shop;
    private static String SIGNATURE = "XEJ6JXQulGC7HhkwU2I95BluJzujFq3SzzYG6eXyPZq7+6Fj+QBPlEmzzk0kuTop+XXzxq9esAoMYz1nsBBd7s0dNbv6tfCi7Y+qc+2+GXUL9knpMvJhulIvFTsTVgWaanqDSyinZ4rvKcTva9wxXbLEIGy8E4KpVlwmrcrS4G2R9E+VPd2jetXQfCrYZqFackegXn0oGglNd2EAPMh80WopwyL6JC9Rjfq4sah9S+agluBWYz3qkbxMDI1+KBeMgJYjZDPg1s60+Kab7qWQkcwziYJvY/UQS6c4KKSWa72O/NHU185NbEowZxYE7W7+MAasQuzDICu/5a13r5mPFgZ7IkDegf9EKDtSFAXqIS/sjH277r2MWe/1s8yFZYRx+Amjxs5sGNSVZQJhv+pqEYCN0tIHrh8h6a9gIv/PgulNe+TMteGzLdLDXfhfZGNl0Ns1FdI06kKRhUl4JmKhTLa0Kr6Vlt8JPTrz1GiVOcf6TzmCLvVt3uOHs92Cd4zGARRpoz0VZVFdLDabmKE89ITgQ2ckkh/iw0dnHet1A8smdRGaKuMDvQUSd/fzCs9wS07gO1wRCEDTXn0EzV7hxr6M1wGVKJEt/na4y3+AcrnLQtO6hUUSV+p/jluiUR0LWYz1i7ScXiLfGx12RTRZpy6Sta7uX1fWok7nn590RJ0=";
    private static String VALUE = "ewogICJ0aW1lc3RhbXAiIDogMTYzNzgzNDg2NjMzMywKICAicHJvZmlsZUlkIiA6ICI0ZTMwZjUwZTdiYWU0M2YzYWZkMmE3NDUyY2ViZTI5YyIsCiAgInByb2ZpbGVOYW1lIiA6ICJfdG9tYXRvel8iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2MzNDdhMTExNDZhYzNlNzM4M2VjNzA0ZjVmYmE5ZjI4NTI3ZDA5YzhiMDI2NWViMDRmYTNjOTllOWIyOWJkYSIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9";



    public ShopNpcPlayer(Location location) {
        shop = new FakePlayer(location);
        shop.setSkin(MojangUtil.createSkinObject(VALUE,SIGNATURE));
        shop.setInvisible(false);
        shop.setClickAction(player -> {
            new UpgradeInventory().openInventory(player);
        });
        shop.spawn();
    }

}
