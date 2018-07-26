package com.johncorby.arenaapi.command;

import com.johncorby.arenaapi.ArenaApiPlugin;
import com.johncorby.arenaapi.arena.Arena;
import com.johncorby.coreapi.command.BaseCommand;
import com.johncorby.coreapi.util.MessageHandler;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Lobby extends BaseCommand {
    public Lobby() {
        super("Teleport to lobby", "", "");
    }

    public static boolean lobby(Player player) {
        // Error if no lobby loc
        Location lL = ArenaApiPlugin.lobby;
        if (lL == null) {
            MessageHandler.error(player, "No lobby location set");
            return false;
        }

        player.teleport(lL);
        MessageHandler.info(player, "Teleported to lobby");
        return true;
    }

    @Override
    public boolean onCommand(Player sender, String[] args) {
        // Leave arena if in one
        Arena aI = Arena.arenaIn(sender);
        if (aI != null) return aI.remove(sender);
        else return lobby(sender);
    }
}
