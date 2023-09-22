package me.joelsoep.ctf.listener;

import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EntityExplodeListener implements Listener {

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.getEntity().getType() == EntityType.PRIMED_TNT) {
            ArrayList<String> list = new ArrayList<>();
            list.add("BLUE_CONCRETE");
            list.add("RED_CONCRETE");
            List<Block> destroyed = event.blockList();
            Iterator<Block> iterator = destroyed.iterator();
            while (iterator.hasNext()) {
                Block block = iterator.next();
                String name = block.getType().toString();
                if (!list.contains(name))
                    iterator.remove();
            }
        }
    }
}
