package me.joelsoep.ctf.util;

import org.bukkit.block.BlockFace;

public class BlockUtils {

    public static float blockFaceToYaw(BlockFace face) {
        switch (face) {
            case SOUTH:      return 0f;
            case SOUTH_WEST: return 45f;
            case WEST:       return 90f;
            case NORTH_WEST: return 135f;
            case NORTH:      return 180f;
            case NORTH_EAST: return -135f; // or 225f
            case EAST:       return -90f;  // or 270f
            case SOUTH_EAST: return -45f;  // or 315f
            default:
                throw new IllegalArgumentException("BlockFace must be horizontal: " + face);
        }
    }

}
