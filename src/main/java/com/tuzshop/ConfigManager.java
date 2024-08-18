package com.tuzshop;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    private final Market plugin;
    private FileConfiguration config;

    public ConfigManager(Market plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        loadDefaultConfig();
    }

    private void loadDefaultConfig() {
        config.addDefault("database.type", "sqlite");
        config.addDefault("database.mysql.host", "localhost");
        config.addDefault("database.mysql.port", 3306);
        config.addDefault("database.mysql.database", "market_db");
        config.addDefault("database.mysql.username", "root");
        config.addDefault("database.mysql.password", "");
        config.addDefault("Market.max_items", 45);
        config.addDefault("Market.hologram_height", 2.5);
        config.options().copyDefaults(true);
        plugin.saveConfig();
    }

    public String getDatabaseType() {
        return config.getString("database.type", "sqlite");
    }

    public String getMySQLHost() {
        return config.getString("database.mysql.host", "localhost");
    }

    public int getMySQLPort() {
        return config.getInt("database.mysql.port", 3306);
    }

    public String getMySQLDatabase() {
        return config.getString("database.mysql.database", "market_db");
    }

    public String getMySQLUsername() {
        return config.getString("database.mysql.username", "root");
    }

    public String getMySQLPassword() {
        return config.getString("database.mysql.password", "");
    }

    public int getMaxMarketItems() {
        return config.getInt("Market.max_items", 45);
    }

    public double getHologramHeight() {
        return config.getDouble("Market.hologram_height", 2.5);
    }
}
