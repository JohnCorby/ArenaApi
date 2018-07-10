package com.johncorby.arenaapi.listener;

import com.johncorby.arenaapi.arena.Arena;
import com.johncorby.coreapi.util.Common;
import com.johncorby.coreapi.util.MessageHandler;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;

public class Block implements Listener {
    // For setting virtualredstone signs
    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        // Ignore if not sign
        if (!(event.getBlock().getState() instanceof Sign)) return;
        Sign s = (Sign) event.getBlock().getState();

        // Ignore if not virtualredstone sign
        if (!s.getLine(0).equalsIgnoreCase(ChatColor.YELLOW + "[GravityGuild]")) return;

        // Try to get arena
        Arena a = Arena.get(s.getLine(1));
        if (a == null) return;

        // Ignore if sign loc doesn't match arena loc
        if (!a.getSign().getLocation().equals(event.getBlock().getLocation())) return;

        // Remove sign from arena
        a.setSign(null);
        MessageHandler.info(event.getPlayer(), "Removed sign for " + Arena.get());
    }


    // For setting virtualredstone signs
    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        // Ignore normal signs
        if (!event.getLine(0).equalsIgnoreCase("[virtualredstone]")) return;

        // Try to get arena
        String aN = event.getLine(1);
        Arena a = Arena.get(aN);
        if (a == null) {
            MessageHandler.error(event.getPlayer(), "Arena " + aN + " doesn't exist");
            return;
        }

        // Ignore if arena already has sign
        if (a.getSign() != null) {
            MessageHandler.error(event.getPlayer(), "Arena " + aN + " already has sign");
            return;
        }

        // Set sign for arena
        event.setLine(0, ChatColor.YELLOW + "[GravityGuild]");
        event.setLine(1, Arena.get());
        event.setLine(2, a.getState().get());
        event.setLine(3, Common.toStr(a.getPlayers().size()));
        a.setSign((Sign) event.getBlock().getState());
        MessageHandler.info(event.getPlayer(), "Set sign for " + aN);
    }
}
