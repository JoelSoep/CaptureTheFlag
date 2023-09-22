package me.joelsoep.ctf.game.advancement;

import com.fren_gor.ultimateAdvancementAPI.AdvancementTab;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface IBaseAdvancement {
    public void registerListener();
    public void giveReward(@NotNull Player player);
}
