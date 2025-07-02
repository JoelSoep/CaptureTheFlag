package me.joelsoep.ctf.util;

import io.papermc.paper.block.BlockPredicate;
import io.papermc.paper.datacomponent.DataComponentBuilder;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemAdventurePredicate;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.keys.tags.BlockTypeTagKeys;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.BlockType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ApiStatus.Experimental
public final class AdventureModeBlockUtils {

    public static void setCanPlaceOn(ItemStack itemStack, BlockType... blockTypes) {
        List<BlockType> blocks = new ArrayList<>(Arrays.asList(blockTypes));

        RegistryKeySet<BlockType> allowed = RegistrySet.keySetFromValues(
                RegistryKey.BLOCK, blocks
        );

        BlockPredicate blockPredicate = BlockPredicate.predicate().blocks(allowed).build();
        ItemAdventurePredicate adventurePredicate = ItemAdventurePredicate.itemAdventurePredicate().addPredicate(blockPredicate).build();
        itemStack.setData(DataComponentTypes.CAN_PLACE_ON, adventurePredicate);
    }

    public static void setCanPlaceOn(ItemStack itemStack, List<BlockType> blockTypes) {
        RegistryKeySet<BlockType> allowed = RegistrySet.keySetFromValues(
                RegistryKey.BLOCK, blockTypes
        );

        BlockPredicate blockPredicate = BlockPredicate.predicate().blocks(allowed).build();
        ItemAdventurePredicate adventurePredicate = ItemAdventurePredicate.itemAdventurePredicate().addPredicate(blockPredicate).build();
        itemStack.setData(DataComponentTypes.CAN_PLACE_ON, adventurePredicate);
    }

    public static void setCanBreak(ItemStack itemStack, BlockType... blockTypes) {
        List<BlockType> blocks = new ArrayList<>(Arrays.asList(blockTypes));

        RegistryKeySet<BlockType> allowed = RegistrySet.keySetFromValues(
                RegistryKey.BLOCK, blocks
        );

        BlockPredicate blockPredicate = BlockPredicate.predicate().blocks(allowed).build();
        ItemAdventurePredicate adventurePredicate = ItemAdventurePredicate.itemAdventurePredicate().addPredicate(blockPredicate).build();
        itemStack.setData(DataComponentTypes.CAN_BREAK, adventurePredicate);
    }

    public static void setCanBreak(ItemStack itemStack, List<BlockType> blockTypes) {
        RegistryKeySet<BlockType> allowed = RegistrySet.keySetFromValues(
                RegistryKey.BLOCK, blockTypes
        );

        BlockPredicate blockPredicate = BlockPredicate.predicate().blocks(allowed).build();
        ItemAdventurePredicate adventurePredicate = ItemAdventurePredicate.itemAdventurePredicate().addPredicate(blockPredicate).build();
        itemStack.setData(DataComponentTypes.CAN_BREAK, adventurePredicate);
    }
}
