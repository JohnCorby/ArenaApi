package com.johncorby.arenaapi.arena;

import org.bukkit.Location;

import static com.johncorby.arenaapi.ArenaApiPlugin.WORLD;
import static com.johncorby.coreapi.CoreApiPlugin.PLUGIN;

public class LobbyHandler {
    // Set lobby loc
    public static void set(Location lobbyLoc) {
        Integer[] iL = new Integer[]{lobbyLoc.getBlockX(), lobbyLoc.getBlockY(), lobbyLoc.getBlockZ(), (int) lobbyLoc.getYaw(), (int) lobbyLoc.getPitch()};
        PLUGIN.getConfig().set("Lobby", iL);
        PLUGIN.saveConfig();
    }

    // Get lobby loc
    public static Location get() {
        // Try to get loc from config
        Integer[] iL = PLUGIN.getConfig().getIntegerList("Lobby").toArray(new Integer[0]);
        if (iL.length == 0) return null;
        return new Location(WORLD, iL[0] + .5, iL[1], iL[2] + .5, iL[3], iL[4]);
    }
}
