package me.joelsoep.ctf.game.kit.kits;

import com.destroystokyo.paper.Namespaced;
import io.papermc.paper.block.BlockPredicate;
import io.papermc.paper.datacomponent.DataComponentBuilder;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemAdventurePredicate;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import me.joelsoep.ctf.game.kit.BaseKit;
import me.joelsoep.ctf.util.AdventureModeBlockUtils;
import me.joelsoep.ctf.util.MM;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.block.BlockType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tom extends BaseKit {
    public Tom() {
        super("Tom", "1.0.0", Material.PUFFERFISH, MM.deserialize("<#4287f5>Tom's kit"));
    }

    @Override
    public void setArmor() {
        armor[0] = new ItemStack(Material.NETHERITE_BOOTS);
        armor[1] = new ItemStack(Material.NETHERITE_LEGGINGS);
        armor[2] = new ItemStack(Material.NETHERITE_CHESTPLATE);
        armor[3] = new ItemStack(Material.NETHERITE_HELMET);
    }

    @Override
    public void setItems() {
        items.put(0, new ItemStack(Material.NETHERITE_SWORD));
        items.put(1, getSigmaWaterBucket());
        items.put(3, new ItemStack(Material.GRAY_CONCRETE));
        items.put(2, getDefaultPickaxe());
        items.put(8, new ItemStack(Material.APPLE, 64));
    }

    @Override
    public void setHand() {
        hand[0] = new ItemStack(Material.NETHERITE_SWORD);
    }

    public ItemStack getSigmaWaterBucket() {
        ItemStack bucket = new ItemStack(Material.WATER_BUCKET);
        ItemMeta bucketMeta = bucket.getItemMeta();
        bucketMeta.setUnbreakable(true);
        bucket.setItemMeta(bucketMeta);
        bucket.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        AdventureModeBlockUtils.setCanPlaceOn(bucket, Registry.BLOCK.stream().toList());
        return bucket;
    }
}
