package me.joelsoep.ctf.database.codec;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.cosmetic.Cosmetic;
import me.joelsoep.ctf.cosmetic.type.CosmeticType;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.player.OfflineCTFPlayer;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.util.*;

public class OfflineCTFPlayerCodec implements Codec<OfflineCTFPlayer> {
    @Override
    public void encode(BsonWriter writer, OfflineCTFPlayer value, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeString("_id", value.getUuid().toString());
        writer.writeString("player_name", value.getName());
        writer.writeInt32("player_kills", value.getKills());
        writer.writeInt32("player_deaths", value.getDeaths());
        writer.writeInt32("flag_captures", value.getCaptures());
        writer.writeDouble("player_kd", value.getKd());
        writer.writeInt32("experience", value.getExperience());
        writer.writeInt32("coins", value.getCoins());
        writer.writeInt32("win_streak", value.getWinStreak());
        writer.writeStartArray("ownedCosmetics");
        value.getOwnedCosmetics().forEach(cosmetic -> {
            String prefix = "cosmetic.";
            String type = cosmetic.getType().getPrefix();
            String name = cosmetic.getName();
            writer.writeString(prefix + type + name);
        });
        writer.writeEndArray();
        writer.writeEndDocument();
    }

    @Override
    public OfflineCTFPlayer decode(BsonReader reader, DecoderContext decoderContext) {
        UUID uuid = null;
        Integer kills = null;
        Integer deaths = null;
        Integer captures = null;
        Double kd = null;
        Integer experience = null;
        Integer coins = null;
        Integer winStreak = null;
        List<Cosmetic> ownedCosmetics = new ArrayList<>();

        reader.readStartDocument();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            String name = reader.readName();
            switch (name) {
                case "_id":
                    uuid = UUID.fromString(reader.readString());
                    break;

                case "player_kills":
                    kills = reader.readInt32();
                    break;

                case "player_deaths":
                    deaths = reader.readInt32();
                    break;

                case "flag_captures":
                    captures = reader.readInt32();
                    break;

                case "player_kd":
                    kd = reader.readDouble();
                    break;

                case "experience":
                    experience = reader.readInt32();
                    break;

                case "coins":
                    coins = reader.readInt32();
                    break;

                case "win_streak":
                    winStreak = reader.readInt32();
                    break;

                case "ownedCosmetics":
                    reader.readStartArray();
                    while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                        String raw = reader.readString();
                        String[] parts = raw.split("\\.");
                        String prefix = parts[0];
                        if (!prefix.toUpperCase(Locale.ROOT).equals("COSMETIC")) {
                            continue;
                        }

                        String typeString = parts[1];
                        String nameString = parts[2];

                        CosmeticType cosmeticType = CosmeticType.valueOf(typeString.toUpperCase(Locale.ROOT));
                        List<Cosmetic> typeCosmetics = Main.getCosmeticsManager().getCosmetics(cosmeticType);
                        if (typeCosmetics == null) {
                            continue;
                        }

                        typeCosmetics.forEach(typeCosmetic -> {
                            String typeCosmeticName = typeCosmetic.getName();
                            if (!Objects.equals(typeCosmeticName, nameString)) {
                                return;
                            }

                            ownedCosmetics.add(typeCosmetic);
                        });
                    }
                    reader.readEndArray();
            }
        }

        OfflineCTFPlayer player = new OfflineCTFPlayer(uuid);
        player.setKills(kills);
        player.setDeaths(deaths);
        player.setCaptures(captures);
        player.setKd(kd);
        player.setExperience(experience);
        player.setCoins(coins);
        player.setWinStreak(winStreak);
        player.setOwnedCosmetics(ownedCosmetics);
        reader.readEndDocument();
        return player;
    }


    @Override
    public Class<OfflineCTFPlayer> getEncoderClass() {
        return OfflineCTFPlayer.class;
    }
}
