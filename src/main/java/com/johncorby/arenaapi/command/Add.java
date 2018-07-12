package com.johncorby.arenaapi.command;

import com.johncorby.arenaapi.arena.SetRegion;
import com.johncorby.coreapi.command.BaseCommand;
import com.johncorby.coreapi.util.MessageHandler;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Add extends BaseCommand {
    public Add() {
        super("Add an arena", "<name>", PERM_ADMIN);
    }

    @Override
    public boolean onCommand(@NotNull Player sender, @NotNull String[] args) {
        // Error if no name given
        if (args.length == 0) {
            MessageHandler.error(sender, "You must give a name for the arena");
            return false;
        }

        // Add arena
        return new SetRegion(sender, args[0], true).exists;
    }
}
