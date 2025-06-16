package me.joelsoep.ctf.util.test;

import me.joelsoep.ctf.Main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
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

    public void rotatingLuckyBlock(int size, int radians) {
        world.spawn(location, BlockDisplay.class, entity -> {
            entity.setBlock(Material.TARGET.createBlockData());
            entity.setTransformation(
                    new Transformation(
                            new Vector3f(),
                            new AxisAngle4f(1F, new Vector3f()),
                            new Vector3f(size, size, size),
                            new AxisAngle4f()
                    )
            );
            task(entity, radians);
        });
    }

    public void task(BlockDisplay display, int radians){
        new BukkitRunnable() {
            double angle = 0;

            @Override
            public void run() {
                if (display.isDead() || !display.isValid()) {
                    cancel();
                    return;
                }

                angle += Math.toRadians(radians); // rotate 5 degrees every tick
                if (angle >= 2 * Math.PI) angle = 0;

                // Create a rotation transformation around the Y-axis
                Transformation transformation = display.getTransformation();
                transformation.getLeftRotation().y = (float) angle;
                display.setTransformation(transformation);
            }
        }.runTaskTimer(Main.getInstance(), 0, 1);
    }


}
