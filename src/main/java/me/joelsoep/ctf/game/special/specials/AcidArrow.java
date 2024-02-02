package me.joelsoep.ctf.game.special.specials;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.special.BaseSpecial;
import me.joelsoep.ctf.game.task.FallingBlockRemovalTask;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.util.ItemBuilder;
import me.joelsoep.ctf.util.MM;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AcidArrow extends BaseSpecial<EntityShootBowEvent> {
    private static final double ARROW_VELOCITY_MULTIPLIER = 1.5;
    private static final double ADDITIONAL_SHOOT_HEIGHT = 1.5;
    private static final long CHAIN_REACTION_DELAY = 5L;
    private static final int MAX_BREAKABLE_BLOCKS = 12;

    public AcidArrow() {
        super("AcidArrow");
    }

    @Override
    public ItemStack getItemStack() {
        return new ItemBuilder(Material.SPECTRAL_ARROW)
                .setName(MM.deserialize("<aqua>Acid Arrow"))
                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ITEM_SPECIFICS)
                .build();
    }

    @Override
    @EventHandler
    public void onUse(EntityShootBowEvent event) {
        Entity livingEntity = event.getEntity();
        Entity projectile = event.getProjectile();
        ItemStack itemStack = event.getConsumable();

        if (!(livingEntity instanceof Player player)) {
            return;
        }

        if (itemStack == null) {
            return;
        }

        if (getItemStack().getType() != itemStack.getType() || !itemStack.hasItemFlag(ItemFlag.HIDE_DYE)) {
            return;
        }
        if (!(projectile instanceof SpectralArrow arrow)) {
            return;
        }

        arrow.customName(MM.deserialize("acid_arrow"));
    }

    @EventHandler
    public void onUse(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof SpectralArrow arrow)) {
            return;
        }
        Component customName = arrow.customName();
        if (customName == null) {
            return;
        }

        if (!(customName.equals(MM.deserialize("acid_arrow")))) {
            return;
        }

        Block block = event.getHitBlock();

        if (block == null) {
            return;
        }

        if (!(block.getType() == Material.BLUE_CONCRETE || block.getType() == Material.RED_CONCRETE)) {
            return;
        }
        Material material = null;

        if (block.getType() == Material.BLUE_CONCRETE) {
            material = Material.BLUE_CONCRETE_POWDER;
        }

        if (block.getType() == Material.RED_CONCRETE) {
            material = Material.RED_CONCRETE_POWDER;
        }

        if (material == null) {
            return;
        }

        event.setCancelled(true);
        destroyBlocks(block, material, MAX_BREAKABLE_BLOCKS);

    }

    @EventHandler
    public void onUse(EntityChangeBlockEvent event) {
        Entity entity = event.getEntity();
        Block block = event.getBlock();

        if (!(entity instanceof FallingBlock fallingBlock)) {
            return;
        }
        if (event.getTo() != Material.RED_CONCRETE_POWDER && event.getTo() != Material.BLUE_CONCRETE_POWDER) {
            return;
        }
        new FallingBlockRemovalTask(fallingBlock);
    }

    public void getBlocks(@NotNull Block block, List<Block> list) {
        getDirectRelatives(block).forEach(member -> {
            LogModule.CORE.log(member.getType().toString());
            if (member.getType() == block.getType()) {
                if (list.contains(member)) {
                    return;
                }
                list.add(member);
                getBlocks(member, list);
            }
        });
    }

    public void destroyBlocks(@NotNull Block block, Material replacement, int amount) {
        List<Block> familyBlocks = new ArrayList<>();
        familyBlocks.add(block);
        getBlocks(block, familyBlocks);
        for (int i = 0; i < amount; i++) {
            if (i >= familyBlocks.size()) {
                return;
            }
            Block breakable = familyBlocks.get(i);
            if (breakable == null) {
                break;
            }
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
                @Override
                public void run() {
                    breakable.setType(replacement);
                }
            }, i * CHAIN_REACTION_DELAY);
        }
    }

    public List<Block> getDirectRelatives(@NotNull Block block) {
        return List.of(
                block.getRelative(BlockFace.UP),
                block.getRelative(BlockFace.DOWN),
                block.getRelative(BlockFace.WEST),
                block.getRelative(BlockFace.EAST),
                block.getRelative(BlockFace.NORTH),
                block.getRelative(BlockFace.SOUTH)
        );
    }
}
