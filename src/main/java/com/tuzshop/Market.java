package com.tuzshop;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;

public class Market extends JavaPlugin {
    private static Market instance;
    private MarketManager marketManager;
    private ConfigManager configManager;
    private DatabaseManager databaseManager;
    private HologramManager hologramManager;
    private RegionManager regionManager;
    @Override
    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();
        configManager = new ConfigManager(this);

        databaseManager = new DatabaseManager(this);
        if (!databaseManager.connect()) {
            getLogger().severe("Veritabanına bağlanılamadı. Eklenti devre dışı bırakılıyor.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        marketManager = new MarketManager(this);
        this.hologramManager = new HologramManager(this);
        regionManager = new RegionManager(this);

        registerCommands();
        registerEvents();

        getLogger().info("Market eklentisi başarıyla etkinleştirildi!");
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.disconnect();
        }
        if (this.hologramManager != null) {
            this.hologramManager.removeAllHolograms();
        }
        getLogger().info("Market eklentisi devre dışı bırakıldı.");
    }

    private void registerCommands() {
        PluginCommand marketCommand = this.getCommand("market");
        if (marketCommand != null) {
            marketCommand.setExecutor(new MarketCommand(this));
        } else {
            getLogger().severe("'market' komutu kaydedilemedi! paper-plugin.yml dosyanızı kontrol edin.");
        }
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new MarketListener(this), this);
    }

    public static Market getInstance() {
        return instance;
    }

    public MarketManager getMarketManager() {
        return marketManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public HologramManager getHologramManager() {
        return hologramManager;
    }

    public RegionManager getRegionManager() {
        return regionManager;
    }
}

