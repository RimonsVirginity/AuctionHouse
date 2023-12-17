package me.rimon.auctionhouse;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AuctionHouseCommand implements CommandExecutor {
    private final AuctionHousePlugin plugin;
    private final AuctionManager auctionManager;

    public AuctionHouseCommand(AuctionHousePlugin plugin, AuctionManager auctionManager) {
        this.plugin = plugin;
        this.auctionManager = auctionManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            // Open the auction house GUI
            AuctionHouseGUI.openAuctionHouse(player, auctionManager);
            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("list")) {
            // Listing an item
            double price;
            try {
                price = Double.parseDouble(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Invalid price. Please enter a valid number.");
                return true;
            }
            ItemStack itemInHand = player.getInventory().getItemInMainHand();

            if (itemInHand == null || itemInHand.getAmount() == 0) {
                player.sendMessage(ChatColor.RED + "You must be holding an item to list it.");
                return true;
            }

            auctionManager.listAuctionItem(itemInHand.clone(), price, player.getName());
            itemInHand.setAmount(itemInHand.getAmount() - 1); // Deduct one item from the stack the player is holding
            player.sendMessage(ChatColor.GREEN + "Item listed in the auction house for " + price + "!");
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Usage: /auctionhouse or /auctionhouse list <price>");
        return true;
    }
}