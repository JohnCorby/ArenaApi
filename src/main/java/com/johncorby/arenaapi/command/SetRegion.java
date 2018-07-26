package com.johncorby.arenaapi.command;

import com.johncorby.arenaapi.arena.Arena;
import com.johncorby.coreapi.command.BaseCommand;
import com.johncorby.coreapi.command.TabCompleteHandler;
import com.johncorby.coreapi.util.MessageHandler;
import org.bukkit.entity.Player;

public class SetRegion extends BaseCommand {
    public SetRegion() {
        super("Set an arena's region", "<name>", PERM_ADMIN);
        TabCompleteHandler.register(getName(), 0, Arena::getNames);
    }

    @Override
    public boolean onCommand(Player sender, String[] args) {
        // Error if no name given
        if (args.length == 0) {
            MessageHandler.error(sender, "You must supply an arena name");
            return false;
        }

        // Try to update region
        return new com.johncorby.arenaapi.arena.SetRegion(sender, args[0], false).exists();
    }
}
