package me.rimon.auctionhouse;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class AuctionHousePlugin extends JavaPlugin {

    private AuctionManager auctionManager;
    private AuctionHouseListener auctionHouseListener;

    @Override
    public void onEnable() {
        // Initialize the AuctionManager
        auctionManager = new AuctionManager(this);
        auctionManager.loadAuctionItems(); // Load the auction items when the plugin is enabled

        // Initialize the listener
        auctionHouseListener = new AuctionHouseListener(this, auctionManager);
        this.getCommand("auctionhouse").setExecutor(new AuctionHouseCommand(this, auctionManager));

        // Get the PluginManager
        PluginManager pm = getServer().getPluginManager();

        // Register the AuctionHouseListener with the PluginManager
        pm.registerEvents(auctionHouseListener, this);

        // Rest of your onEnable logic, such as registering commands
    }

    @Override
    public void onDisable() {
        // Save auction items when the plugin is disabled
        auctionManager.saveAuctionItems();
    }

    public AuctionManager getAuctionManager() {
        return auctionManager;
    }
}