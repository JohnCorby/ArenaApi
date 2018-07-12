package com.johncorby.arenaapi.command;

import com.johncorby.arenaapi.arena.LobbyHandler;
import com.johncorby.coreapi.command.BaseCommand;
import com.johncorby.coreapi.util.MessageHandler;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetLobby extends BaseCommand {
    public SetLobby() {
        super("Set lobby location", "", PERM_ADMIN);
    }

    @Override
    public boolean onCommand(@NotNull Player sender, String[] args) {
        LobbyHandler.set(sender.getLocation());
        MessageHandler.info(sender, "Lobby location set");
        return true;
    }
}
