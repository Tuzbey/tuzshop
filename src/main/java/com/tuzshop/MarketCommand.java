package com.tuzshop;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MarketCommand implements CommandExecutor {
    private final Market plugin;

    public MarketCommand(Market plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Bu komut sadece oyuncular tarafından kullanılabilir.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            sendHelpMessage(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "create":
                plugin.getMarketManager().createMarket(player, player.getLocation());
                break;
            case "remove":
                plugin.getMarketManager().removeMarket(player);
                break;
            case "additem":
                if (args.length < 2) {
                    player.sendMessage("Kullanım: /Market additem <fiyat>");
                    return true;
                }
                try {
                    double price = Double.parseDouble(args[1]);
                    ItemStack itemInHand = player.getInventory().getItemInMainHand();
                    if (itemInHand.getType().isAir()) {
                        player.sendMessage("Elinizde bir eşya tutmalısınız.");
                        return true;
                    }
                    MarketItem marketItem = new MarketItem(itemInHand, price);
                    plugin.getMarketManager().addItemToMarket(player, marketItem);
                } catch (NumberFormatException e) {
                    player.sendMessage("Geçerli bir fiyat giriniz.");
                }
                break;
            case "removeitem":
                if (args.length < 2) {
                    player.sendMessage("Kullanım: /Market removeitem <index>");
                    return true;
                }
                try {
                    int index = Integer.parseInt(args[1]);
                    plugin.getMarketManager().removeItemFromMarket(player, index);
                } catch (NumberFormatException e) {
                    player.sendMessage("Geçerli bir index giriniz.");
                }
                break;
            case "list":
                // Pazar ürünlerini listele
                // Bu kısmı implement etmeniz gerekecek
                break;
            default:
                sendHelpMessage(player);
                break;
        }

        return true;
    }

    private void sendHelpMessage(Player player) {
        player.sendMessage("§6=== Market Komutları ===");
        player.sendMessage("§e/Market create §7- Pazar oluştur");
        player.sendMessage("§e/Market remove §7- Pazarı kaldır");
        player.sendMessage("§e/Market additem <fiyat> §7- Elinizdeki eşyayı pazara ekle");
        player.sendMessage("§e/Market removeitem <index> §7- Pazardan eşya kaldır");
        player.sendMessage("§e/Market list §7- Pazar ürünlerini listele");
    }
}
