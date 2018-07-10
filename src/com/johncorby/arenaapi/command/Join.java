package com.johncorby.arenaapi.command;

import com.johncorby.arenaapi.arena.Arena;
import com.johncorby.coreapi.command.BaseCommand;
import com.johncorby.coreapi.command.TabCompleteHandler;
import com.johncorby.coreapi.util.MessageHandler;
import org.bukkit.entity.Player;

public class Join extends BaseCommand {
    public Join() {
        super("Join an arena", "<name>", "");
        TabCompleteHandler.register(getName(), 0, Arena::getNames);
    }

    @Override
    public boolean onCommand(Player sender, String[] args) {
        // Error if no arena name
        if (args.length == 0) {
            MessageHandler.error(sender, "You must supply an arena name");
            return false;
        }

        // Try to get arena
        Arena a = Arena.get(args[0]);
        if (a == null) {
            MessageHandler.error(sender, "Arena " + args[0] + " does not exist");
            return false;
        }

        return a.add(sender);
    }
}
