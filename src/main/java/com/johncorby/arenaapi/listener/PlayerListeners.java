package com.johncorby.arenaapi.listener;

import com.johncorby.arenaapi.arena.Arena;
import com.johncorby.coreapi.util.MessageHandler;
import com.johncorby.coreapi.util.Runnable;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerListeners implements Listener {
    @EventHandler
    public void onInteract(@NotNull PlayerInteractEvent event) {
        // If right clicked block
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            // Ignore if not sign
            if (!(event.getClickedBlock().getState() instanceof Sign)) return;
            Sign s = (Sign) event.getClickedBlock().getState();

            // Ignore normal signs
            if (!s.getLine(0).equalsIgnoreCase(MessageHandler.prefix)) return;

            // Try to join game
            Arena.get(s.getLine(1)).add(event.getPlayer());
        }

    }


    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        org.bukkit.entity.Player player = event.getPlayer();
        // Try to get arena and make player leave it
        Arena aI = Arena.arenaIn(player);
        if (aI != null) aI.remove(player);
    }


    // Notify about bugs being present
    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        new Runnable() {
            @Override
            public void run() {
                MessageHandler.warn(event.getPlayer(), "=========================================",
                        "Post bugs/features at https://github.com/JohnCorby/GravityGuild/issues",
                        "=========================================");
            }
        }.runTaskLater(10);
    }
}
