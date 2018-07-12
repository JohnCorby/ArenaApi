package com.johncorby.arenaapi.command;

import com.johncorby.arenaapi.arena.Arena;
import com.johncorby.coreapi.command.BaseCommand;
import com.johncorby.coreapi.command.TabCompleteHandler;
import com.johncorby.coreapi.util.MessageHandler;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Delete extends BaseCommand {
    public Delete() {
        super("Delete an arena", "<name>", PERM_ADMIN);
        TabCompleteHandler.register(getName(), 0, Arena::getNames);
    }

    @Override
    public boolean onCommand(@NotNull Player sender, @NotNull String[] args) {
        // Error if no name given
        if (args.length == 0) {
            MessageHandler.error(sender, "You must supply an arena name");
            return false;
        }

        // Error if arena doesn't exist
        if (Arena.get(args[0]) == null) {
            MessageHandler.error(sender, "Arena " + args[0] + " does not exist");
            return false;
        }

        // Delete arena
        MessageHandler.info(sender, "Arena " + args[0] + " deleted");
        return Arena.get(args[0]).dispose();
    }
}
