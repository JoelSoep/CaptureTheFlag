package me.joelsoep.ctf.map;

import io.papermc.lib.PaperLib;
import lombok.Getter;
import lombok.Setter;
import me.joelsoep.ctf.file.FileManager;
import me.joelsoep.ctf.util.EmptyChunkGenerator;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

import javax.annotation.Nullable;

@Getter
public abstract class Map {

    protected final String name;
    protected final String prefix;
    @Setter protected World world;
    @Setter protected MapState state;
    @Setter private boolean enabled;

    public Map(String name, String prefix) {
        this.name = name;
        this.prefix = prefix;
        this.state = MapState.LOADING;
        this.world = null;
    }

    public @Nullable World createEmptyWorld() {
        WorldCreator worldCreator = new WorldCreator(FileManager.getWorldFolderPath() + prefix + name);
        worldCreator.type(WorldType.FLAT);
        worldCreator.generator(new EmptyChunkGenerator());
        World world = Bukkit.createWorld(worldCreator);
        if (world == null) {
            Bukkit.getLogger().warning("World %s is null, attempting to get access to it...".formatted(prefix + name));
            world = Bukkit.getWorld(prefix + name);
        }

        if (world == null) {
            Bukkit.getLogger().severe("FATAL: World %s couldn't be found. Aborting world creation...".formatted(prefix + name));
            return null;
        }
        world.setKeepSpawnInMemory(false);
        PaperLib.getChunkAtAsync(world.getSpawnLocation(), true);
        return world;
    }

    private void registerToDatabase() {
        // TODO: Make it so that worlds can be enabled / disabled on startup
    }
}
