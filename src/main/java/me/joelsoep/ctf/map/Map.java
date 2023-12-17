package me.joelsoep.ctf.map;

import lombok.Getter;
import lombok.Setter;
import me.joelsoep.ctf.util.EmptyChunkGenerator;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@Getter
public abstract class Map {

    protected final String name;
    protected final String prefix;
    @Setter protected World world;
    @Setter private boolean enabled;

    public Map(String name, String prefix) {
        this.name = name;
        this.prefix = prefix;
        this.world = null;
    }

    public @Nullable World createEmptyWorld() {
        WorldCreator worldCreator = new WorldCreator(prefix + name);
        worldCreator.type(WorldType.FLAT);
        worldCreator.generator(new EmptyChunkGenerator());
        return Bukkit.createWorld(worldCreator);
    }

    private void registerToDatabase() {
        // TODO: Make it so that worlds can be enabled / disabled on startup
    }
}
