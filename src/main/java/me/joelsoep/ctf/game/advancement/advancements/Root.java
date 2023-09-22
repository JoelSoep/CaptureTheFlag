package me.joelsoep.ctf.game.advancement.advancements;

import com.fren_gor.ultimateAdvancementAPI.AdvancementTab;
import com.fren_gor.ultimateAdvancementAPI.advancement.RootAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementDisplay;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementFrameType;
import com.fren_gor.ultimateAdvancementAPI.events.PlayerLoadingCompletedEvent;
import me.joelsoep.ctf.Main;
import org.bukkit.Material;

public class Root extends RootAdvancement {
    public Root() {
        super(
                Main.advancementAPI.getTab(),
                "root",
                new AdvancementDisplay(
                        Material.PLAYER_HEAD,
                        "Capture the Flag",
                        AdvancementFrameType.TASK,
                        true,
                        true,
                        0,
                        2,
                        "You've logged on for the first time!"
                ),
                "textures/block/stone.png",
                1);
        AdvancementTab tab = Main.advancementAPI.getTab();
        tab.getEventManager().register(Main.advancementAPI.getTab(), PlayerLoadingCompletedEvent.class, event -> {
            tab.showTab(event.getPlayer());
            tab.grantRootAdvancement(event.getPlayer());
        });
    }
}
