package com.johncorby.arenaapi;

import com.johncorby.arenaapi.arena.Arena;
import com.johncorby.arenaapi.arena.ArenaEvents;
import com.johncorby.arenaapi.command.*;
import com.johncorby.arenaapi.listener.Block;
import com.johncorby.arenaapi.listener.Player;
import com.johncorby.coreapi.CoreApiPlugin;
import com.johncorby.coreapi.command.BaseCommand;
import com.johncorby.coreapi.util.Common;
import com.johncorby.coreapi.util.MessageHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Listener;

import java.util.Set;

public abstract class ArenaApiPlugin extends CoreApiPlugin {
    public static World WORLD;
    public static Set<String> OVERRIDE_PLAYERS;
    public static Location lobby;

    public static Set<org.bukkit.entity.Player> getOverridePlayers() {
        return Common.toSet(Common.map(OVERRIDE_PLAYERS, Bukkit::getPlayer));
    }

    @Override
    public void onEnable() {
        super.onEnable();

        Arena.ARENA_EVENTS = getArenaEvents();

        // Set up config
        getConfig().options().copyDefaults(true);
        saveConfig();

        // Load stuff from config
        WORLD = getServer().getWorld(getConfig().getString("World"));

        OVERRIDE_PLAYERS = Common.toSet(getConfig().getStringList("OverridePlayers"));

        lobby = (Location) PLUGIN.getConfig().get("Lobby");

//        for (String name : Arena.ARENA_SECTION.getKeys(false)) {
//            ConfigurationSection arena = Arena.ARENA_SECTION.getConfigurationSection(name);
//            Integer[] region = arena.getIntegerList("Region").toArray(new Integer[0]);
//            Location signLoc = (Location) arena.get("SignLoc");
//            new Arena(name, region, signLoc);
//        }
        for (Object o : getConfig().getList("Arenas")) {
            if (!(o instanceof Arena)) continue;
            Arena a = (Arena) o;

            MessageHandler.debug("Got arena " + a);
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
