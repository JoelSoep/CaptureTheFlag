package me.joelsoep.ctf.util;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import me.joelsoep.ctf.file.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class FAWE {

    private static boolean failed = false;

    /**
     *
     * @param file The schematic to paste.
     * @param location The location where the schematic should be pasted.
     * @return A boolean which indicates whether the operation was successful.
     */

    public static boolean paste(File file, Location location) {

        ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(file);
        Clipboard clipboard;

        BlockVector3 blockVector3 = BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ());

        if (clipboardFormat != null) {
            try (ClipboardReader clipboardReader = clipboardFormat.getReader(new FileInputStream(file))) {

                if (location.getWorld() == null)
                    throw new NullPointerException("Failed to paste schematic due to world being null");

                World world = BukkitAdapter.adapt(location.getWorld());

                EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(world).build();

                clipboard = clipboardReader.read();

                Operation operation = new ClipboardHolder(clipboard)
                        .createPaste(editSession)
                        .to(blockVector3)
                        .ignoreAirBlocks(false)
                        .build();

                try {
                    Operations.complete(operation);
                    editSession.close();
                    failed = false;
                } catch (WorldEditException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                if (e instanceof FileNotFoundException) {
                    Bukkit.getLogger().severe("Couldn't find file " + file.getName() + "!");
                    if (failed) {
                        Bukkit.getLogger().severe("Attempt to get file %s failed! Cannot load world.");
                        return false;
                    }
                    Bukkit.getLogger().severe("Attempting to download file %s...".formatted(file.getName()));
                    FileManager.requestFile(file.getName(), "schematics" + File.separator + file.getName());
                    failed = true;
                    paste(file, location);
                    return true;
                }
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
}
