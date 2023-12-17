package me.rimon.auctionhouse;

import org.bukkit.inventory.ItemStack;
import java.util.UUID;

public class AuctionItem {
    private final ItemStack item;
    private final double price;
    private final String sellerName;
    private final UUID id;

    public AuctionItem(ItemStack item, double price, String sellerName) {
        this.item = item;
        this.price = price;
        this.sellerName = sellerName;
        this.id = UUID.randomUUID(); // Assign a unique ID to each item
    }

    // Getters
    public ItemStack getItem() {
        return item;
    }

    public double getPrice() {
        return price;
    }

    public String getSellerName() {
        return sellerName;
    }

    public UUID getId() {
        return id;
    }
}
