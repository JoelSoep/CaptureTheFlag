package me.joelsoep.ctf.game.special.specials;

import me.joelsoep.ctf.game.special.BaseSpecial;
import me.joelsoep.ctf.util.ItemBuilder;
import me.joelsoep.ctf.util.MM;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class KitUpgrade extends BaseSpecial<PlayerInteractEvent> {
    public KitUpgrade() {
        super("KitUpgrade");
    }

    @Override
    protected ItemStack getItemStack() {
        return new ItemBuilder(Material.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE)
                .setName(MM.deserialize("<#ae4aff>Kit Upgrade"))
                .build();
    }

    @Override
    public void onUse(PlayerInteractEvent event) {

    }
}
