package me.rimon.auctionhouse;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class AuctionManager {
    private final AuctionHousePlugin plugin;
    private final List<AuctionItem> auctionItems;

    public AuctionManager(AuctionHousePlugin plugin) {
        this.plugin = plugin;
        this.auctionItems = new ArrayList<>();
    }

    public void listAuctionItem(ItemStack item, double price, String sellerName) {
        AuctionItem auctionItem = new AuctionItem(item, price, sellerName);
        auctionItems.add(auctionItem);
    }

    public AuctionItem getAuctionItem(UUID id) {
        return auctionItems.stream()
                .filter(item -> item.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void removeAuctionItem(AuctionItem auctionItem) {
        auctionItems.remove(auctionItem);
    }

    public void saveAuctionItems() {
        FileConfiguration config = new YamlConfiguration();
        List<String> serializedItems = auctionItems.stream().map(auctionItem -> {
            String itemStackString = itemStackToBase64(auctionItem.getItem());
            return auctionItem.getSellerName() + ";" + auctionItem.getPrice() + ";" + itemStackString;
        }).collect(Collectors.toList());

        config.set("auctionItems", serializedItems);
        try {
            config.save(new File(plugin.getDataFolder(), "auctions.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadAuctionItems() {
        File auctionFile = new File(plugin.getDataFolder(), "auctions.yml");
        if (!auctionFile.exists()) return;

        FileConfiguration config = YamlConfiguration.loadConfiguration(auctionFile);
        List<String> serializedItems = config.getStringList("auctionItems");
        auctionItems.clear();

        for (String serializedItem : serializedItems) {
            String[] parts = serializedItem.split(";");
            if (parts.length < 3) continue;

            String sellerName = parts[0];
            double price = Double.parseDouble(parts[1]);
            ItemStack itemStack = base64ToItemStack(parts[2]);

            auctionItems.add(new AuctionItem(itemStack, price, sellerName));
        }
    }
    private String itemStackToBase64(ItemStack item) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(item);
            dataOutput.close();
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stack.", e);
        }
    }
    private ItemStack base64ToItemStack(String data) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack item = (ItemStack) dataInput.readObject();
            dataInput.close();
            return item;
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("Unable to read item stack from base64.", e);
        }
    }

    public List<AuctionItem> getAuctionItems() {
        return auctionItems;
    }

    public void sortAuctionItems() {
        auctionItems.sort(Comparator.comparingDouble(AuctionItem::getPrice));
    }
}
