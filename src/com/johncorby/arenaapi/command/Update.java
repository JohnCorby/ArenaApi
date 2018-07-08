package com.johncorby.arenaapi.command;

import com.johncorby.arenaapi.arena.Arena;
import com.johncorby.coreapi.command.BaseCommand;
import com.johncorby.coreapi.command.TabCompleteHandler;
import com.johncorby.coreapi.util.MessageHandler;
import org.bukkit.entity.Player;

public class Update extends BaseCommand {
    public Update() {
        super("Update arena blocks", "<name>", "gg.admin");
        TabCompleteHandler.register(getName(), 0, Arena::getNames);
    }

    @Override
    public boolean onCommand(Player sender, String[] args) {
        // Error if no name given
        if (args.length == 0) {
            MessageHandler.error(sender, "You must supply an arena name");
            return false;
        }

        // Error if arena doesn't exist
        Arena a = Arena.get(args[0]);
        if (a == null) {
            MessageHandler.error(sender, "Arena " + args[0] + " does not exist");
            return false;
        }

        // Try to update region
        a.setRegion(a.getRegion());
        MessageHandler.info(sender, "Updated arena blocks");
        return true;
    }
}
