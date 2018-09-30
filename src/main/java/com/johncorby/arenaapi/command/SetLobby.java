package com.johncorby.arenaapi.command;

import com.johncorby.coreapi.command.BaseCommand;
import com.johncorby.coreapi.util.MessageHandler;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.johncorby.arenaapi.ArenaApiPlugin.lobby;
import static com.johncorby.coreapi.CoreApiPlugin.PLUGIN;

public class SetLobby extends BaseCommand {
    public SetLobby() {
        super("Set lobby location", "", PERM_ADMIN);
    }

    @Override
    public boolean onCommand(@NotNull Player sender, String[] args) {
        lobby = sender.getLocation();
        PLUGIN.getConfig().set("Lobby", lobby);
        PLUGIN.saveConfig();
        MessageHandler.info(sender, "Lobby location set");
        return true;
    }
}
