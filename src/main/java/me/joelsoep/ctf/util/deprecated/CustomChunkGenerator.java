package me.joelsoep.ctf.util.deprecated;

import io.papermc.lib.environments.Environment;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.map.MapManager;
import me.joelsoep.ctf.util.RandomUtil;
import org.bukkit.Bukkit;
import org.bukkit.HeightMap;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Deprecated
public class CustomChunkGenerator extends ChunkGenerator {

    public CustomChunkGenerator(String name, Biome biom) {
        super();
        World world = MapManager.createEmptyWorld(name);
        if (world == null) {
            LogModule.MAP.log("bruhsdafsdf");
            return;
        }
       // this.generateNoise(getInfo(world), RandomUtil.get(), 0, 0, getData(world));
       // this.generateSurface(getInfo(world), RandomUtil.get(), 0, 0, getData(world));
        //this.generateSurface(getInfo(world), RandomUtil.get(), 0, 0, getData(world));

    }

    @Override
    public boolean shouldGenerateMobs() {
        return false;
    }

    @Override
    public boolean shouldGenerateStructures() {
        return false;
    }

    /**public ChunkData getData(World world) {
        if (world == null) {
            LogModule.MAP.log("world is null @ data");
            return null;
        }
        ChunkData data = createChunkData(world);
        data.setRegion(0, 90, 0, 50, 100, 50, Material.GRASS_BLOCK);
        return data;
    }

    public WorldInfo getInfo(World world) {
        if (world == null) {
            LogModule.MAP.log("worldi s null");
            return null;
        }

        //return new WorldInfo() {
           // @Override
           // public @NotNull String getName() {
          //      return Main.getGame().getUuid().toString();
          //  }
/**
            @Override
            public @NotNull UUID getUID() {
                return Main.getGame().getUuid();
            }

            @Override
            public World.@NotNull Environment getEnvironment() {
                return World.Environment.NORMAL;
            }

            @Override
            public long getSeed() {
                return -1673046740;
            }

            @Override
            public int getMinHeight() {
                return 90;
            }

            @Override
            public int getMaxHeight() {
                return 100;
            }

            @Override
            public @NotNull BiomeProvider vanillaBiomeProvider() {
                return Objects.requireNonNull(Main.getInstance().getDefaultBiomeProvider(world.getName(), Main.getGame().getUuid().toString()));
            }
return null;
        };

    }

   /** public BiomeProvider provider() {
         return new BiomeProvider() {
             @Override
             public @NotNull Biome getBiome(@NotNull WorldInfo worldInfo, int x, int y, int z) {
                 return Biome.SUNFLOWER_PLAINS;
             }

             @Override
             public @NotNull List<Biome> getBiomes(@NotNull WorldInfo worldInfo) {
                 return List.of(
                         Biome.SUNFLOWER_PLAINS,
                         Biome.JUNGLE,
                         Biome.FLOWER_FOREST
                 );
                 //List<Biome> allBiomes = List.copyOf(Arrays.stream(Biome.values()).toList());
             }
         };
    };

    **/
}
