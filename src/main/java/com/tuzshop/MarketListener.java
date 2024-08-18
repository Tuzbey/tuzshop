package com.tuzshop;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.NamespacedKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MarketListener implements Listener {
    private final Market plugin;
    private final NamespacedKey marketChestKey;

    public MarketListener(Market plugin) {
        this.plugin = plugin;
        this.marketChestKey = new NamespacedKey(plugin, "market_chest");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        if (event.getClickedBlock() == null) return;

        // Özel sandık kontrolü
        if (event.getClickedBlock().getType() == Material.CHEST) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (isMarketChest(item)) {
                event.setCancelled(true);
                createMarket(player, event.getClickedBlock().getLocation());
                return;
            }
        }

        PlayerMarket market = plugin.getMarketManager().getPlayerMarket(player.getUniqueId());
        if (market != null && market.getLocation().getBlock().equals(event.getClickedBlock())) {
            event.setCancelled(true);
            openMarketMenu(player, market);
        }
    }

    private boolean isMarketChest(ItemStack item) {
        if (item == null || item.getType() != Material.CHEST) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.has(marketChestKey, PersistentDataType.BYTE);
    }

    private void createMarket(Player player, Location location) {
        if (plugin.getRegionManager().canCreateMarketAt(location)) {
            plugin.getMarketManager().createMarket(player, location);
            player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
            player.sendMessage("Pazar başarıyla oluşturuldu!");
        } else {
            player.sendMessage("Bu bölgede pazar oluşturamazsınız.");
        }
    }

    private void openMarketMenu(Player player, PlayerMarket market) {
        // Pazar menüsünü aç
        Inventory marketInventory = Bukkit.createInventory(null, 54, player.getName() + "'in Pazarı");

        // Pazardaki ürünleri menüye ekle
        for (int i = 0; i < market.getItems().size() && i < 45; i++) {
            MarketItem marketItem = market.getItems().get(i);
            ItemStack displayItem = marketItem.getItem().clone();
            ItemMeta meta = displayItem.getItemMeta();
            List<String> lore = meta.getLore() != null ? meta.getLore() : new ArrayList<>();
            lore.add("§eFiyat: §a" + marketItem.getPrice() + " TL");
            meta.setLore(lore);
            displayItem.setItemMeta(meta);
            marketInventory.setItem(i, displayItem);
        }

        // Menü kontrol butonlarını ekle
        marketInventory.setItem(49, createGuiItem(Material.BARRIER, "§cKapat", "§7Menüyü kapatmak için tıkla"));
        if (player.getUniqueId().equals(market.getOwnerId())) {
            marketInventory.setItem(51, createGuiItem(Material.CHEST, "§aÜrün Ekle", "§7Yeni ürün eklemek için tıkla"));
            marketInventory.setItem(52, createGuiItem(Material.REDSTONE, "§cÜrün Kaldır", "§7Ürün kaldırmak için tıkla"));
        }

        player.openInventory(marketInventory);
    }

    private ItemStack createGuiItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }
}

