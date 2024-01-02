package me.joelsoep.ctf.game.advancement;

import com.fren_gor.ultimateAdvancementAPI.AdvancementTab;
import com.fren_gor.ultimateAdvancementAPI.UltimateAdvancementAPI;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.logger.LogModule;
import org.bukkit.plugin.Plugin;

import java.util.Set;

public class AdvancementAPI {

    private UltimateAdvancementAPI api = null;
    private final Plugin plugin;
    private AdvancementTab tab;

    public AdvancementAPI(Plugin plugin) {
        this.plugin = plugin;
     //   this.api = UltimateAdvancementAPI.getInstance(plugin);
      //  if (this.api == null) {
     //       LogModule.ADVANCEMENT.severe("UltimateAdvancementAPI is not enabled or not found.");
            // Handle the error or disable the CTF plugin if necessary.
    //    } else {
    //        this.tab = api.createAdvancementTab("ctf");
   //     }
    }

    public void registerTab() {
        this.tab.registerAdvancements(
                Main.advancementService.getRoot(),
                Set.copyOf(Main.advancementService.getAdvancements().values())
        );
    }

    public UltimateAdvancementAPI getAPI() {
        return this.api;
    }

    public AdvancementTab getTab() {
        return this.tab;
    }

}
