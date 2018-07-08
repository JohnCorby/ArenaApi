package com.johncorby.arenaapi;

import com.johncorby.arenaapi.command.*;
import com.johncorby.arenaapi.event.Block;
import com.johncorby.arenaapi.event.Player;
import com.johncorby.coreapi.CoreApiPlugin;
import com.johncorby.coreapi.command.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.World;
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
    public abstract String getMessagePrefix();

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
}
