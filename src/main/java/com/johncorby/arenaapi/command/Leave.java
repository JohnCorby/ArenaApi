package com.johncorby.arenaapi.command;

import com.johncorby.arenaapi.arena.Arena;
import com.johncorby.coreapi.command.BaseCommand;
import com.johncorby.coreapi.util.MessageHandler;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Leave extends BaseCommand {
    public Leave() {
        super("Leave the arena you're in", "", "");
    }

    @Override
    public boolean onCommand(@NotNull Player sender, String[] args) {
        // Try to get arena that player is in
        Arena aI = Arena.arenaIn(sender);
        if (aI == null) {
            MessageHandler.error(sender, "You're not in any arena");
            return false;
        }

        // Leave and tp to lobby
        return aI.remove(sender);
    }

}
