package me.joelsoep.ctf.game.advancement.advancements.captures;

import com.fren_gor.ultimateAdvancementAPI.AdvancementTab;
import com.fren_gor.ultimateAdvancementAPI.advancement.BaseAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementDisplay;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementFrameType;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.advancement.IBaseAdvancement;
import me.joelsoep.ctf.game.event.FlagCaptureEvent;
import org.bukkit.Material;

public class Capturer extends BaseAdvancement implements IBaseAdvancement {

    private final AdvancementTab tab;

    public Capturer() {
        super(
                "winner_1",
                new AdvancementDisplay(
                        Material.GREEN_BANNER,
                        "A Great Thief",
                        AdvancementFrameType.TASK,
                        true,
                        true,
                        1,
                        1,
                        "Capture a flag for your team!"
                ),
                Main.advancementService.getRoot(),
                1
        );
        this.tab = Main.advancementAPI.getTab();
    }

    @Override
    public void registerListener() {
        this.tab.getEventManager().register(this.tab, FlagCaptureEvent.class, event -> {
            incrementProgression(event.getCtfPlayer().getPlayer());
        });
    }
}
