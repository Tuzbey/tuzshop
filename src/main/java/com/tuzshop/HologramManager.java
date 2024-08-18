package com.tuzshop;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HologramManager {
    private final Market plugin;
    private final Map<UUID, Hologram> marketHolograms;

    public HologramManager(Market plugin) {
        this.plugin = plugin;
        this.marketHolograms = new HashMap<>();
    }

    public void createMarketHologram(PlayerMarket market) {
        Location loc = market.getLocation().clone().add(0, plugin.getConfigManager().getHologramHeight(), 0);
        Hologram hologram = DHAPI.createHologram(market.getOwnerId().toString(), loc);
        updateHologramContent(hologram, market);
        marketHolograms.put(market.getOwnerId(), hologram);
    }

    public void updateMarketHologram(PlayerMarket market) {
        Hologram hologram = marketHolograms.get(market.getOwnerId());
        if (hologram != null) {
            updateHologramContent(hologram, market);
        }
    }

    private void updateHologramContent(Hologram hologram, PlayerMarket market) {
        List<String> lines = generateHologramLines(market);
        DHAPI.setHologramLines(hologram, lines);
    }

    private List<String> generateHologramLines(PlayerMarket market) {
        // Pazar bilgilerini içeren satırları oluştur
        // Bu metodu pazarınızın görünümüne göre özelleştirmeniz gerekecek
        return List.of(
                "§6§l" + plugin.getServer().getOfflinePlayer(market.getOwnerId()).getName() + "'in Pazarı",
                "§eÜrün Sayısı: §f" + market.getItems().size(),
                "§7Alışveriş yapmak için tıkla!"
        );
    }

    public void removeMarketHologram(PlayerMarket market) {
        Hologram hologram = marketHolograms.remove(market.getOwnerId());
        if (hologram != null) {
            DHAPI.removeHologram(hologram.getName());
        }
    }

    public void removeAllHolograms() {
        for (Hologram hologram : marketHolograms.values()) {
            DHAPI.removeHologram(hologram.getName());
        }
        marketHolograms.clear();
    }
}
