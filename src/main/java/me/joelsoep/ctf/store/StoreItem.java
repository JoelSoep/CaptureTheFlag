package me.joelsoep.ctf.store;

import lombok.Getter;
import lombok.Setter;
import me.joelsoep.ctf.logger.LogModule;
import org.bukkit.Material;

import java.time.Instant;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

@Getter
public class StoreItem {
    private final String prefix;
    private final String name;
    @Setter private int price;
    @Setter private double discountCoefficient;
    @Setter private boolean available; // whether the item should appear in stores.
    @Setter private Date expiryDate;

    @Setter private String displayName;
    @Setter private Material display;

    public StoreItem(String prefix, String name, int price, double discountCoefficient) {
        this.prefix = prefix;
        this.name = name;
        this.price = price;
        this.discountCoefficient = discountCoefficient;
        available = false;
        setNotExpire();

        this.displayName = name.split("\\.")[1];
        this.display = Material.LIME_DYE;
    }

    public StoreItem(String prefix, String name, int price, double discountCoefficient, boolean available) {
        this.prefix = prefix;
        this.name = name;
        this.price = price;
        this.discountCoefficient = discountCoefficient;
        this.available = available;
        setNotExpire();

        this.displayName = name.split("\\.")[1];
        this.display = Material.LIME_DYE;
    }

    public StoreItem(String prefix, String name, int price, double discountCoefficient, boolean available, Date expiryDate) {
        this.prefix = prefix;
        this.name = name;
        this.price = price;
        this.discountCoefficient = discountCoefficient;
        this.available = available;
        this.expiryDate = expiryDate;

        this.displayName = name.split("\\.")[1];
        this.display = Material.LIME_DYE;
    }

    public boolean isFree() {
        return (this.getPrice() * this.getDiscountCoefficient()) <= 0;
    }

    public void setNotExpire() {
        this.expiryDate = Date.from(Instant.parse("9999-12-31T23:59:59.999Z"));
    }

    public void setExpire() {
        this.expiryDate = Date.from(Instant.EPOCH);
    }

    public boolean isExpiring() {
        return expiryDate.equals(Date.from(Instant.EPOCH));
    }

    public void checkAndProcessAvailability() {
        this.available = this.expiryDate.getTime() >= System.currentTimeMillis();
    }

    public StoreItem toStoreItem() {
        StoreItem item = new StoreItem(
                this.getPrefix(),
                this.getName(),
                this.getPrice(),
                this.getDiscountCoefficient(),
                this.isAvailable(),
                this.getExpiryDate()
        );
        item.setDisplay(this.getDisplay());
        item.setDisplayName(this.getDisplayName());
        return item;
    }
}
