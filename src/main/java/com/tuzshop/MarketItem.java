package com.tuzshop;

import org.bukkit.inventory.ItemStack;

public class MarketItem {
    private final ItemStack item;
    private final double price;

    public MarketItem(ItemStack item, double price) {
        this.item = item;
        this.price = price;
    }

    public ItemStack getItem() {
        return item.clone();
    }

    public double getPrice() {
        return price;
    }
}

