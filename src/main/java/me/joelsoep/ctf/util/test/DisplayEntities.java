package me.joelsoep.ctf.util.test;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

public class DisplayEntities {

    private final Location location;
    private final World world;

    public DisplayEntities(Location location) {
        this.location = location;
        this.world = location.getWorld();
    }

    public void scale(Material material, int scale) {
        world.spawn(location, BlockDisplay.class, entity -> {
            entity.setBlock(material.createBlockData());
            entity.setTransformation(
                    new Transformation(
                            new Vector3f(),
                            new AxisAngle4f(),
                            new Vector3f(scale, scale, scale),
                            new AxisAngle4f()
                    )
            );
        });
    }


}
