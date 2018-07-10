package com.johncorby.arenaapi;

import com.johncorby.arenaapi.arena.Arena;
import com.johncorby.arenaapi.arena.ArenaEvents;
import com.johncorby.arenaapi.command.*;
import com.johncorby.arenaapi.listener.Block;
import com.johncorby.arenaapi.listener.Player;
import com.johncorby.coreapi.CoreApiPlugin;
import com.johncorby.coreapi.command.BaseCommand;
import com.johncorby.coreapi.util.MessageHandler;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class ArenaApiPlugin extends CoreApiPlugin {
    public static World WORLD;

    public static String[] overridePlayers = {"johncorby", "funkymunky111"};

    public static Set<org.bukkit.entity.Player> getOverridePlayers() {
        return Arrays.stream(overridePlayers)
                .map(p -> Bukkit.getServer().getPlayer(p))
                .collect(Collectors.toSet());
    }

    @Override
    public void onEnable() {
        super.onEnable();

        Arena.arenaEvents = getArenaEvents();

        // Set up config
        getConfig().options().copyDefaults(true);
        saveConfig();

        // Load stuff from config
        WORLD = getServer().getWorld(getConfig().getString("World"));

        for (String name : Arena.arenaSection.getKeys(false)) {
            ConfigurationSection arena = Arena.arenaSection.getConfigurationSection(name);
            Integer[] region = arena.getIntegerList("Region").toArray(new Integer[0]);
            Integer[] signLoc = arena.getIntegerList("SignLoc").toArray(new Integer[0]);
            new Arena(name, region, signLoc);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();

        // Stop all arenas
        for (Arena a : Arena.getArenas()) {
            for (org.bukkit.entity.Player p : a.getPlayers())
                MessageHandler.warn(p, "You have been forced out of the arena because the plugin was reloaded (probably for debugging)");
            a.setState(Arena.State.STOPPED);
        }
    }

    @Override
    public BaseCommand[] getCommands() {
        return new BaseCommand[]{
                new Lobby(),
                new Join(),
                new Leave(),

                new Add(),
                new Cancel(),
                new Delete(),
                new SetRegion(),
                new Update(),
                new SetLobby()
        };
    }

    @Override
    public Listener[] getListeners() {
        return new Listener[]{
                new Block(),
                new Player(),
        };
    }

    public abstract ArenaEvents getArenaEvents();
}
