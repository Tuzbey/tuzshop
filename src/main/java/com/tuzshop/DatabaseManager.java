package com.tuzshop;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DatabaseManager {
    private final Market plugin;
    private Connection connection;

    public DatabaseManager(Market plugin) {
        this.plugin = plugin;
    }

    public boolean connect() {
        try {
            if (plugin.getConfigManager().getDatabaseType().equalsIgnoreCase("mysql")) {
                connectToMySQL();
            } else {
                connectToSQLite();
            }
            createTables();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void connectToMySQL() throws SQLException {
        String host = plugin.getConfigManager().getMySQLHost();
        int port = plugin.getConfigManager().getMySQLPort();
        String database = plugin.getConfigManager().getMySQLDatabase();
        String username = plugin.getConfigManager().getMySQLUsername();
        String password = plugin.getConfigManager().getMySQLPassword();
        String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
        connection = DriverManager.getConnection(url, username, password);
    }

    private void connectToSQLite() throws SQLException {
        String url = "jdbc:sqlite:" + plugin.getDataFolder().getAbsolutePath() + "/Market.db";
        connection = DriverManager.getConnection(url);
    }

    private void createTables() throws SQLException {
        String createMarketsTable = "CREATE TABLE IF NOT EXISTS markets (" +
                "owner_id VARCHAR(36) PRIMARY KEY," +
                "world VARCHAR(50)," +
                "x DOUBLE," +
                "y DOUBLE," +
                "z DOUBLE" +
                ")";
        String createItemsTable = "CREATE TABLE IF NOT EXISTS market_items (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "owner_id VARCHAR(36)," +
                "item_data TEXT," +
                "price DOUBLE," +
                "FOREIGN KEY (owner_id) REFERENCES markets(owner_id)" +
                ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createMarketsTable);
            stmt.execute(createItemsTable);
        }
    }

    public void saveMarket(PlayerMarket market) {
        String sql = "INSERT OR REPLACE INTO markets (owner_id, world, x, y, z) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, market.getOwnerId().toString());
            pstmt.setString(2, market.getLocation().getWorld().getName());
            pstmt.setDouble(3, market.getLocation().getX());
            pstmt.setDouble(4, market.getLocation().getY());
            pstmt.setDouble(5, market.getLocation().getZ());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeMarket(PlayerMarket market) {
        String sql = "DELETE FROM markets WHERE owner_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, market.getOwnerId().toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveMarketItem(PlayerMarket market, MarketItem item) {
        String sql = "INSERT INTO market_items (owner_id, item_data, price) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, market.getOwnerId().toString());
            pstmt.setString(2, itemStackToBase64(item.getItem()));
            pstmt.setDouble(3, item.getPrice());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeMarketItem(PlayerMarket market, MarketItem item) {
        String sql = "DELETE FROM market_items WHERE owner_id = ? AND item_data = ? AND price = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, market.getOwnerId().toString());
            pstmt.setString(2, itemStackToBase64(item.getItem()));
            pstmt.setDouble(3, item.getPrice());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<UUID, PlayerMarket> loadAllMarkets() {
        Map<UUID, PlayerMarket> markets = new HashMap<>();
        String sql = "SELECT * FROM markets";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                UUID ownerId = UUID.fromString(rs.getString("owner_id"));
                String world = rs.getString("world");
                double x = rs.getDouble("x");
                double y = rs.getDouble("y");
                double z = rs.getDouble("z");
                Location location = new Location(Bukkit.getWorld(world), x, y, z);
                PlayerMarket market = new PlayerMarket(ownerId, location);
                loadMarketItems(market);
                markets.put(ownerId, market);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return markets;
    }

    private void loadMarketItems(PlayerMarket market) {
        String sql = "SELECT * FROM market_items WHERE owner_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, market.getOwnerId().toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ItemStack item = itemStackFromBase64(rs.getString("item_data"));
                    double price = rs.getDouble("price");
                    market.addItem(new MarketItem(item, price));
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String itemStackToBase64(ItemStack item) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(item);
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stack.", e);
        }
    }

    private ItemStack itemStackFromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack item = (ItemStack) dataInput.readObject();
            dataInput.close();
            return item;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }
}
