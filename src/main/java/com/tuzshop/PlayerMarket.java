package com.tuzshop;

import org.bukkit.Location;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerMarket {
    private final UUID ownerId;
    private final Location location;
    private final List<MarketItem> items;

    public PlayerMarket(UUID ownerId, Location location) {
        this.ownerId = ownerId;
        this.location = location;
        this.items = new ArrayList<>();
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public Location getLocation() {
        return location;
    }

    public List<MarketItem> getItems() {
        return new ArrayList<>(items);
    }

    public void addItem(MarketItem item) {
        items.add(item);
    }

    public MarketItem removeItem(int index) {
        if (index >= 0 && index < items.size()) {
            return items.remove(index);
        }
        return null;
    }
}
