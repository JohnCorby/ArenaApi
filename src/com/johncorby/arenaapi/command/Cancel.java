package com.johncorby.arenaapi.command;

import com.johncorby.arenaapi.arena.SetRegion;
import com.johncorby.coreapi.command.BaseCommand;
import com.johncorby.coreapi.util.MessageHandler;
import org.bukkit.entity.Player;

public class Cancel extends BaseCommand {
    public Cancel() {
        super("Cancel arena region setting", "", PERM_ADMIN);
    }

    @Override
    public boolean onCommand(Player sender, String[] args) {
        // Error if setting region
        SetRegion sR = SetRegion.get(sender);
        if (sR == null) {
            MessageHandler.error(sender, "You're not setting an arena region");
            return false;
        }

        // Remove region setter
        MessageHandler.info(sender, "Cancelled region setting for arena " + sR.name);
        return SetRegion.get(sender).dispose();
    }
}
