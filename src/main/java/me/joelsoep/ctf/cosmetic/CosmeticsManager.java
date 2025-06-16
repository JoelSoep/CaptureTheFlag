package me.joelsoep.ctf.cosmetic;

import lombok.Getter;
import me.joelsoep.ctf.cosmetic.type.CosmeticType;
import me.joelsoep.ctf.cosmetic.type.DeathMessageCosmetic;
import me.joelsoep.ctf.listener.ListenerService;
import me.joelsoep.ctf.logger.LogModule;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class CosmeticsManager {

    @Getter private static final HashMap<CosmeticType, HashMap<String, Cosmetic>> cosmetics = new HashMap<>();

    public CosmeticsManager() {
        new ListenerService("me.joelsoep.ctf.cosmetic.type.listener");
        loadDeathMessages();
    }

    public @Nullable Cosmetic getCosmetic(CosmeticType type, String name) {
        HashMap<String, Cosmetic> map = cosmetics.get(type);
        if (map == null) {
            return null;
        }

        return map.get(name);
    }

    public @Nullable List<Cosmetic> getCosmetics(CosmeticType type) {
        HashMap<String, Cosmetic> map = cosmetics.get(type);
        if (map == null) {
            return null;
        }

        return List.copyOf(map.values());
    }

    public void addCosmetic(CosmeticType type, Cosmetic cosmetic) {
        HashMap<String, Cosmetic> map = cosmetics.get(type);
        if (map == null) {
            map = new HashMap<>();
        }

        map.put(cosmetic.getName(), cosmetic);
        cosmetics.put(type, map);
    }

    /**
     *
     * TODO: add annotation instead of a class for each single deathmessage cosmetic.
     */
    private void loadDeathMessages() {
        String dir = "me.joelsoep.ctf.cosmetic.type.deathMessage";
        Reflections reflections = new Reflections(dir);

        Set<Class<? extends DeathMessageCosmetic>> classes = reflections.getSubTypesOf(DeathMessageCosmetic.class);
        classes.forEach(cosmeticClass -> {
            try {
                DeathMessageCosmetic cosmetic = cosmeticClass.getDeclaredConstructor().newInstance();
                this.addCosmetic(cosmetic.getType(), cosmetic);
                LogModule.COSMETIC.log("Loaded deathMessage cosmetic %s!".formatted(cosmetic.getName()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}