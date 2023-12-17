package me.rimon.auctionhouse;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuctionHouseGUI {

    public static final String INVENTORY_TITLE = ChatColor.BLUE + "Auction House";

    public static void openAuctionHouse(Player player, AuctionManager auctionManager) {
        Inventory inventory = Bukkit.createInventory(null, 54, INVENTORY_TITLE);
        auctionManager.sortAuctionItems(); // Optional: Sort items before displaying

        for (AuctionItem auctionItem : auctionManager.getAuctionItems()) {
            ItemStack itemStack = auctionItem.getItem().clone();
            ItemMeta meta = itemStack.getItemMeta();

            if (meta != null) {
                List<String> lore = meta.getLore() != null ? meta.getLore() : new ArrayList<>();
                lore.add(ChatColor.GOLD + "Price: " + auctionItem.getPrice());
                lore.add(ChatColor.DARK_GREEN + "Seller: " + auctionItem.getSellerName());
                lore.add(ChatColor.DARK_PURPLE + "ID: " + auctionItem.getId().toString());
                meta.setLore(lore);
                itemStack.setItemMeta(meta);
            }

            inventory.addItem(itemStack);
        }

        player.openInventory(inventory);
    }

    public static UUID getAuctionIdFromItem(ItemStack item) {
        if (item != null && item.hasItemMeta() && item.getItemMeta().hasLore()) {
            List<String> lore = item.getItemMeta().getLore();
            for (String line : lore) {
                if (line.contains("ID: ")) {
                    String idStr = ChatColor.stripColor(line).replace("ID: ", "").trim();
                    try {
                        return UUID.fromString(idStr);
                    } catch (IllegalArgumentException e) {
                        // Handle the case where the UUID is not valid
                    }
                }
            }
        }
        return null;
    }
}
