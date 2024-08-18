package com.tuzshop;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MarketManager {
    private final Market plugin;
    private final Map<UUID, PlayerMarket> playerMarkets;

    public MarketManager(Market plugin) {
        this.plugin = plugin;
        this.playerMarkets = new HashMap<>();
        loadMarketsFromDatabase();
    }

    public void createMarket(Player player, Location location) {
        if (!plugin.getRegionManager().canCreateMarketAt(location)) {
            player.sendMessage("Bu bölgede pazar kuramazsınız.");
            return;
        }

        UUID playerId = player.getUniqueId();
        if (playerMarkets.containsKey(playerId)) {
            player.sendMessage("Zaten bir pazarınız var.");
            return;
        }

        PlayerMarket market = new PlayerMarket(playerId, location);
        playerMarkets.put(playerId, market);
        plugin.getDatabaseManager().saveMarket(market);
        plugin.getHologramManager().createMarketHologram(market);
        player.sendMessage("Pazarınız başarıyla oluşturuldu.");
    }

    public void removeMarket(Player player) {
        UUID playerId = player.getUniqueId();
        PlayerMarket market = playerMarkets.remove(playerId);
        if (market != null) {
            plugin.getDatabaseManager().removeMarket(market);
            plugin.getHologramManager().removeMarketHologram(market);
            player.sendMessage("Pazarınız kaldırıldı.");
        } else {
            player.sendMessage("Aktif bir pazarınız yok.");
        }
    }

    public void addItemToMarket(Player player, MarketItem item) {
        PlayerMarket market = playerMarkets.get(player.getUniqueId());
        if (market != null) {
            market.addItem(item);
            plugin.getDatabaseManager().saveMarketItem(market, item);
            plugin.getHologramManager().updateMarketHologram(market);
            player.sendMessage("Ürün pazara eklendi.");
        } else {
            player.sendMessage("Aktif bir pazarınız yok.");
        }
    }

    public void removeItemFromMarket(Player player, int itemIndex) {
        PlayerMarket market = playerMarkets.get(player.getUniqueId());
        if (market != null) {
            MarketItem removedItem = market.removeItem(itemIndex);
            if (removedItem != null) {
                plugin.getDatabaseManager().removeMarketItem(market, removedItem);
                plugin.getHologramManager().updateMarketHologram(market);
                player.sendMessage("Ürün pazardan kaldırıldı.");
            } else {
                player.sendMessage("Belirtilen indekste ürün bulunamadı.");
            }
        } else {
            player.sendMessage("Aktif bir pazarınız yok.");
        }
    }

    private void loadMarketsFromDatabase() {
        // Veritabanından pazarları yükle
        Map<UUID, PlayerMarket> loadedMarkets = plugin.getDatabaseManager().loadAllMarkets();
        playerMarkets.putAll(loadedMarkets);
        for (PlayerMarket market : loadedMarkets.values()) {
            plugin.getHologramManager().createMarketHologram(market);
        }
    }

    public PlayerMarket getPlayerMarket(UUID playerId) {
        return playerMarkets.get(playerId);
    }

    public Map<UUID, PlayerMarket> getAllMarkets() {
        return new HashMap<>(playerMarkets);
    }
}
