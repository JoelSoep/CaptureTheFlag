package me.joelsoep.ctf.game.task;

import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.map.type.GameMap;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RemoveItemDisplayFlagsTask implements Runnable {

    private final GameMap map;

    public RemoveItemDisplayFlagsTask(GameMap map) {
        this.map = map;
    }

    @Override
    public void run() {
        AtomicInteger amount = new AtomicInteger();
        Location[] locations = new Location[2];
        locations[0] = map.getBlueFlagLocation();
        locations[1] = map.getRedFlagLocation();

        for (Location location : locations) {
            Chunk chunk = location.getChunk();
            chunk.load();
            List<Entity> entities = Arrays.asList(chunk.getEntities());
            entities.forEach(entity -> {
                if (!(entity instanceof ItemDisplay)) {
                    return;
                }
                entity.remove();
                amount.getAndIncrement();
            });
        }

        LogModule.CORE.log("Cleared %s item displays for gamemap %s.".formatted(amount.get(), map.getName()));
    }
}
