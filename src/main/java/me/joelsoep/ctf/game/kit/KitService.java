package me.joelsoep.ctf.game.kit;

import me.joelsoep.ctf.CTFPlayer;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.util.MM;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.reflections.Reflections;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Level;

public class KitService {
    private final Map<String, BaseKit> kits;

    public KitService() {
        this.kits = new HashMap<>();
        loadKits("me.joelsoep.ctf.game.kit.kits");
    }

    private void loadKits(String packageName) {
        // Use reflection to find all classes that extend BaseKit in the specified package
        Reflections reflections = new Reflections(packageName);
        Set<Class<? extends BaseKit>> kitClasses = reflections.getSubTypesOf(BaseKit.class);

        // Instantiate and load all found kit classes
        for (Class<? extends BaseKit> kitClass : kitClasses) {
            try {
                BaseKit kit = kitClass.getDeclaredConstructor().newInstance();
                kits.put(kit.getName().toLowerCase(), kit);
                Bukkit.getLogger().log(Level.INFO, "Loaded kit " + kit.getName());
            } catch (InstantiationException | IllegalAccessException
                    | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    public List<BaseKit> getAllKits() {
        return new ArrayList<>(kits.values());
    }

    public BaseKit getKit(String kitName) {
        return kits.get(kitName.toLowerCase());
    }

    public void giveKitToPlayer(Player player, String kitName) {
        BaseKit kit = getKit(kitName);
        if (kit != null) {
            kit.giveKitToPlayer(player);
            return;
        }
        MM.deserialize("<#f2180c>This kit doesn't exist. Please report this to an administrator.");
    }

    public @Nullable BaseKit getAppliedKit(Player player) {
        CTFPlayer CTFPlayer = Main.playerList.get(player.getName());
        return CTFPlayer.getKit();
    }
}