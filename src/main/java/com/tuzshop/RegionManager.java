package com.tuzshop;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;

public class RegionManager {
    private final Market plugin;

    public RegionManager(Market plugin) {
        this.plugin = plugin;
    }

    public boolean canCreateMarketAt(Location location) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(com.sk89q.worldedit.bukkit.BukkitAdapter.adapt(location));

        for (ProtectedRegion region : set) {
            if (region.getId().equalsIgnoreCase("market_zone")) {
                return true;
            }
        }
        return false;
    }
}
