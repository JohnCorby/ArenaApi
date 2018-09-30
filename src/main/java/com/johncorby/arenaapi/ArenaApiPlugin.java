package com.johncorby.arenaapi;

import com.johncorby.arenaapi.arena.Arena;
import com.johncorby.arenaapi.arena.ArenaEvents;
import com.johncorby.arenaapi.command.*;
import com.johncorby.arenaapi.listener.BlockListeners;
import com.johncorby.arenaapi.listener.PlayerListeners;
import com.johncorby.coreapi.CoreApiPlugin;
import com.johncorby.coreapi.command.BaseCommand;
import com.johncorby.coreapi.util.Common;
import com.johncorby.coreapi.util.Config;
import com.johncorby.coreapi.util.MessageHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

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

        ConfigurationSerialization.registerClass(Arena.class);

        // Set up config
        getConfig().options().copyDefaults(true);
        saveConfig();

        // Load stuff from config
        WORLD = getServer().getWorld(getConfig().getString("World"));

        OVERRIDE_PLAYERS = Common.toSet(getConfig().getStringList("OverridePlayers"));

        lobby = (Location) PLUGIN.getConfig().get("Lobby");

        Config.getSet("Arenas");
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


    @NotNull
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


    @NotNull
    @Override
    public Listener[] getListeners() {
        return new Listener[]{
                new BlockListeners(),
                new PlayerListeners(),
        };
    }


    @NotNull
    public abstract ArenaEvents getArenaEvents();
}
