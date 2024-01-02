package me.joelsoep.ctf.game.advancement;

import com.fren_gor.ultimateAdvancementAPI.AdvancementTab;
import com.fren_gor.ultimateAdvancementAPI.UltimateAdvancementAPI;
import com.fren_gor.ultimateAdvancementAPI.advancement.BaseAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.RootAdvancement;
import lombok.Getter;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.logger.LogModule;
import org.bukkit.Bukkit;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public class AdvancementService {

  //  private final UltimateAdvancementAPI api;
  //  private final AdvancementTab tab;
   @Getter private Map<String, BaseAdvancement> advancements;
   @Getter private RootAdvancement root;

    /**
     * Currently disabled.
     */

    public AdvancementService() {
        this.advancements = new HashMap<>();
     //   this.api = Main.advancementAPI.getAPI();
     //   this.tab = Main.advancementAPI.getTab();
      //  Main.advancementService = this;
       // loadRootAdvancement("me.joelsoep.ctf.game.advancement.advancements");
       // loadBaseAdvancements("me.joelsoep.ctf.game.advancement.advancements");

        /**
         * Do not ask me why this is the case, but if this code is removed / altered the plugin crashes.
         */
        if (Main.advancementService == null) {
            LogModule.ADVANCEMENT.warning("ADVANCEMENT_SERVICE IS NULL!");
            LogModule.ADVANCEMENT.warning("Setting advancementService to this to fix error.");
            Main.advancementService = this;
        }
        Main.advancementAPI.registerTab();
    }

    private void loadRootAdvancement(String packageName) {
        // Use reflection to find all classes that extend BaseKit in the specified package
        Reflections reflections = new Reflections(packageName);
        Set<Class<? extends RootAdvancement>> advancementClass = reflections.getSubTypesOf(RootAdvancement.class);

        // Instantiate and load all found kit classes
            try {
                if (advancementClass.stream().findFirst().isEmpty()) {
                    for (int i = 0; i < 5; i++) {
                        LogModule.ADVANCEMENT.severe("FAILURE | Couldn't find root advancement!");
                    }
                }
                RootAdvancement advancement = advancementClass.stream().findFirst().get().getDeclaredConstructor().newInstance();
                this.root = advancement;

                LogModule.ADVANCEMENT.log(Level.INFO, "Registered root advancement with key " + advancement.getKey());
            } catch (InstantiationException | IllegalAccessException
                    | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
    }

    private void loadBaseAdvancements(String packageName) {
        // Use reflection to find all classes that extend BaseKit in the specified package
        Reflections reflections = new Reflections(packageName);
        Set<Class<? extends BaseAdvancement>> advancementClasses = reflections.getSubTypesOf(BaseAdvancement.class);

        // Instantiate and load all found kit classes
        for (Class<? extends BaseAdvancement> advancementClass : advancementClasses) {
            try {
                BaseAdvancement advancement = advancementClass.getDeclaredConstructor().newInstance();
                advancement.getClass().getMethod("registerListener").invoke(advancement);
                advancements.put(advancement.getKey().getKey(), advancement);

                LogModule.ADVANCEMENT.log("Registered base advancement with key " + advancement.getKey());
            } catch (InstantiationException | IllegalAccessException
                    | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }
}
