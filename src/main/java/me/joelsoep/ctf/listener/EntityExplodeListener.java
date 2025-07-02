package me.joelsoep.ctf.listener;

import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.Iterator;
import java.util.List;

public class EntityExplodeListener implements Listener {
    private static final List<String> DESTROYABLE_BLOCKS = List.of(
            "BLUE_CONCRETE",
            "RED_CONCRETE",
            "BLUE_CONCRETE_POWDER",
            "RED_CONCRETE_POWDER"
    );

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.getEntity().getType() != EntityType.TNT) {
            return;
        }

        List<Block> destroyed = event.blockList();
        Iterator<Block> iterator = destroyed.iterator();

        while (iterator.hasNext()) {
            Block block = iterator.next();
            String name = block.getType().toString();
            if (!DESTROYABLE_BLOCKS.contains(name))
                iterator.remove();
        }
    }
}
