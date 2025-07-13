package me.joelsoep.ctf.game.setting;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.event.PlayerVoteSettingEvent;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.util.MM;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.codehaus.plexus.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import javax.annotation.Nullable;
import java.util.*;

public class GameSettingsManager {

    private static final String handlersPackage = "me.joelsoep.ctf.game.setting.handler";
    private final EnumMap<GameSetting, GameSettingImpl<?>> handlers;
    private final EnumMap<GameSetting, Object> settings;

    private final EnumMap<GameSetting, HashMap<CTFPlayer, Object>> votes;

    public GameSettingsManager() {
        this.settings = new EnumMap<>(GameSetting.class);
        this.handlers = new EnumMap<>(GameSetting.class);
        this.votes = new EnumMap<>(GameSetting.class);
        this.setDefaults();
        this.loadHandlers();
    }

    public void setSetting(GameSetting setting, Object value) {
        settings.put(setting, value);
    }

    public Object getSetting(GameSetting setting) {
        return settings.get(setting);
    }

    public void reset() {
        this.settings.clear();
        this.votes.clear();
        this.setDefaults();
    }

    public void setDefaults() {
        for (GameSetting setting : GameSetting.values()) {
            settings.put(setting, setting.getDefault());
        }
    }

    public @Nullable Object getVote(@NotNull CTFPlayer player, @NotNull GameSetting setting) {
        HashMap<CTFPlayer, Object> settingVotes = votes.get(setting);
        if (settingVotes == null) {
            return null;
        }

        return settingVotes.get(player);
    }

    public boolean hasVotedFor(@NotNull CTFPlayer player, @NotNull GameSetting setting, @Nullable Object value) {
        return Objects.equals(value, getVote(player, setting));
    }

    public void vote(@NotNull CTFPlayer player, @NotNull GameSetting setting, @Nullable Object value) {
        @Nullable HashMap<CTFPlayer, Object> voteMap = this.votes.get(setting);
        if (voteMap == null) {
            HashMap<CTFPlayer, Object> newMap = new HashMap<>();
            newMap.put(player, value);
            this.votes.put(setting, newMap);
            PlayerVoteSettingEvent event = new PlayerVoteSettingEvent(player, setting, value);
            Bukkit.getPluginManager().callEvent(event);
            return;
        }

        voteMap.put(player, value);
        this.votes.put(setting, voteMap);
        PlayerVoteSettingEvent event = new PlayerVoteSettingEvent(player, setting, value);
        Bukkit.getPluginManager().callEvent(event);
    }

    public int getVotesFor(GameSetting setting, Object value) {
        HashMap<CTFPlayer, Object> settingVotes = votes.get(setting);
        if(settingVotes == null || settingVotes.isEmpty()) {
            return 0;
        }

        int votes = 0;
        for (Object vote : settingVotes.values()) {
            if (Objects.equals(vote, value)) {
                votes++;
            }
        }
        return votes;
    }

    public void announceDecisions(Audience audience) {
        List<String> messages = new ArrayList<>();
        for (GameSetting setting : settings.keySet()) {
            if (Objects.equals(setting.getDefault(), settings.get(setting))) {
                continue;
            }

            String formattedSetting = StringUtils.capitalise(setting.toString().replace("_", " ").toLowerCase(Locale.ROOT));
            String message = "<yellow>%s<gray>: <yellow>%s".formatted(formattedSetting, settings.get(setting));
            messages.add(message);
        }
        if (messages.isEmpty()) {
            return;
        }
        audience.sendMessage(MM.deserialize("<gray>Settings: "));
        for (String message : messages) {
            audience.sendMessage(MM.deserialize(message));
        }
    }

    public void removeVotesFor(CTFPlayer player) {
        for (GameSetting setting : votes.keySet()) {
            HashMap<CTFPlayer, Object> map = votes.get(setting);
            map.remove(player);
        }
    }

    public void calculateVotesAndSet() {
        for (GameSetting setting : votes.keySet()) {
            HashMap<CTFPlayer, Object> settingVotes = votes.get(setting);
            if (settingVotes == null || settingVotes.isEmpty()) {
                continue;
            }

            HashMap<Object, Integer> optionVotes = new HashMap<>();
            for (Object object : settingVotes.values()) {
                if (object == null) {
                    continue;
                }

                if (optionVotes.get(object) == null) {
                    optionVotes.put(object, 1);
                } else {
                    Integer amount = optionVotes.get(object);
                    amount = amount + 1;
                    optionVotes.put(object, amount);
                }
            }

            if (optionVotes.isEmpty()) {
                continue;
            }

            Map.Entry<Object, Integer> decision = null;
            for (Map.Entry<Object, Integer> entry : optionVotes.entrySet()) {
                if (decision == null) {
                    decision = entry;
                    continue;
                }

                if (entry.getValue() > decision.getValue()) {
                    decision = entry;
                }
            }

            this.setSetting(setting, decision.getKey());
        }
    }

    private void loadHandlers() {
        Reflections reflections = new Reflections(handlersPackage, Scanners.SubTypes);
        Set<Class<? extends GameSettingImpl>> classes = reflections.getSubTypesOf(GameSettingImpl.class);
        classes.forEach(handlerClass -> {
            if (!Listener.class.isAssignableFrom(handlerClass)) {
                throw new IllegalStateException("handlerclass %s does not implement the listener class.".formatted(handlerClass.getName()));
            }

            try {
                GameSettingImpl<?> handler = handlerClass.getDeclaredConstructor().newInstance();
                GameSetting setting = handler.getSetting();
                if (handlers.get(setting) != null) {
                    throw new IllegalStateException("Tried to register a duplicate gamesetting handler for setting %s with class %s.".formatted(setting.toString(), handlerClass.getName()));
                }

                Bukkit.getPluginManager().registerEvents((Listener) handler, Main.getInstance());
                handlers.put(setting, handler);
                LogModule.GAME.log("Registered gamesetting handler %s for setting %s".formatted(handlerClass.getName(), setting.toString()));
            } catch (Exception e) {
                LogModule.GAME.log("An error occurred while initializing gamesetting handler class %s".formatted(handlerClass.getName()));
                e.printStackTrace();
            }
        });
    }
}
