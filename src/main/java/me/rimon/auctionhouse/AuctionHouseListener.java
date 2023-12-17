package me.rimon.auctionhouse;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class AuctionHouseListener implements Listener {
    private final AuctionHousePlugin plugin;
    private final AuctionManager auctionManager;

    public AuctionHouseListener(AuctionHousePlugin plugin, AuctionManager auctionManager) {
        this.plugin = plugin;
        this.auctionManager = auctionManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        // Check if the click was in the Auction House inventory
        if (event.getView().getTitle().equals(AuctionHouseGUI.INVENTORY_TITLE)) {
            event.setCancelled(true); // Prevent moving items in the auction GUI

            if (clickedItem == null || !clickedItem.hasItemMeta()) return;

            UUID itemId = AuctionHouseGUI.getAuctionIdFromItem(clickedItem);
            if (itemId == null) {
                player.sendMessage(ChatColor.RED + "This item is no longer available.");
                return;
            }

            AuctionItem auctionItem = auctionManager.getAuctionItem(itemId);
            if (auctionItem == null) {
                player.sendMessage(ChatColor.RED + "This item is no longer available.");
                return;
            }

            double price = auctionItem.getPrice();
            if (!hasEnoughMoney(player, price)) {
                player.sendMessage(ChatColor.RED + "You do not have enough money to buy this item.");
                return;
            }

            if (attemptToGiveItem(player, auctionItem.getItem())) {
                auctionManager.removeAuctionItem(auctionItem);
                deductMoney(player, price);
                player.sendMessage(ChatColor.GREEN + "You have bought " + auctionItem.getItem().getType().toString() + " for " + price + ".");
                AuctionHouseGUI.openAuctionHouse(player, auctionManager); // Refresh the GUI
            } else {
                player.sendMessage(ChatColor.RED + "You do not have enough inventory space.");
            }
        }
    }

    private boolean hasEnoughMoney(Player player, double price) {
        // Placeholder for checking if the player has enough money
        // Integrate with your economy plugin (like Vault)
        // Example: return plugin.getEconomy().has(player, price);
        return true; // Implement actual economy check
    }

    private void deductMoney(Player player, double price) {
        // Placeholder for deducting money from the player
        // Integrate with your economy plugin (like Vault)
        // Example: plugin.getEconomy().withdrawPlayer(player, price);
    }

    private boolean attemptToGiveItem(Player player, ItemStack itemStack) {
        // Check if the player has space in their inventory
        if (player.getInventory().firstEmpty() == -1) {
            return false;
        }

        // Give the player the item
        player.getInventory().addItem(itemStack);
        return true;
    }
}