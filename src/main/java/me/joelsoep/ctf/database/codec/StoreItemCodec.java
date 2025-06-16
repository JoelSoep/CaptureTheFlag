package me.joelsoep.ctf.database.codec;

import lombok.extern.java.Log;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.store.StoreItem;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bukkit.Material;

import java.util.Date;

public class StoreItemCodec implements Codec<StoreItem> {

    @Override
    public void encode(BsonWriter writer, StoreItem value, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeString("_id", value.getName());
        writer.writeInt32("price", value.getPrice());
        writer.writeDouble("discountCoefficient", value.getDiscountCoefficient());
        writer.writeBoolean("available", value.isAvailable());
        writer.writeDateTime("expiryDate", value.getExpiryDate().getTime());
        LogModule.CORE.log(value.getDisplayName());
        LogModule.CORE.log(value.getDisplay().toString());
        writer.writeString("displayName", value.getDisplayName());
        writer.writeString("displayMaterial", value.getDisplay().toString());
        writer.writeEndDocument();
    }

    @Override
    public StoreItem decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();
        String name = reader.readString("_id");
        int price = reader.readInt32("price");
        double discountCoefficient = reader.readDouble("discountCoefficient");
        boolean available = reader.readBoolean("available");
        Date expiryDate = new Date(reader.readDateTime("expiryDate"));
        String displayName = reader.readString("displayName");
        Material displayMaterial = Material.getMaterial(reader.readString("displayMaterial"));

        StoreItem item = new StoreItem(name, price, discountCoefficient, available, expiryDate);
        item.setDisplayName(displayName);
        item.setDisplay(displayMaterial);
        reader.readEndDocument();
        return item;
    }

    @Override
    public Class<StoreItem> getEncoderClass() {
        return StoreItem.class;
    }
}
