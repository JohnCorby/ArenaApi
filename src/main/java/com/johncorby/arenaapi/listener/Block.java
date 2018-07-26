package com.johncorby.arenaapi.listener;

import com.johncorby.arenaapi.arena.Arena;
import com.johncorby.coreapi.util.MessageHandler;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;

import static com.johncorby.coreapi.CoreApiPlugin.PLUGIN;

public class Block implements Listener {
    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Sign s = (Sign) event.getBlock().getState();

        s.setLine(0, event.getLine(0));
        s.setLine(1, event.getLine(1));
        s.setLine(2, event.getLine(2));
        s.setLine(3, event.getLine(3));
        s.update(true, false);
        onPlace(new BlockPlaceEvent(s.getBlock(), null, null, null, event.getPlayer(), true, null));
    }


    // For setting arena signs
    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        // Ignore if not sign
        if (!(event.getBlock().getState() instanceof Sign)) return;
        Sign s = (Sign) event.getBlock().getState();

        // Ignore normal signs
        if (!s.getLine(0).equalsIgnoreCase('[' + PLUGIN.getName() + ']')) return;

        // Try to get arena
        String aN = s.getLine(1);
        Arena a = Arena.get(aN);
        if (a == null) {
            MessageHandler.error(event.getPlayer(), "Arena " + aN + " doesn't exist");
            return;
        }

        // Set sign for arena
        a.setSign(s);
        MessageHandler.info(event.getPlayer(), "Set sign for " + aN);
    }


    // For unsetting arena signs
    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        // Ignore if not sign
        if (!(event.getBlock().getState() instanceof Sign)) return;
        Sign s = (Sign) event.getBlock().getState();

        // Ignore normal signs
        if (!s.getLine(0).equalsIgnoreCase(MessageHandler.prefix)) return;

        // Try to get arena
        Arena a = Arena.get(s.getLine(1));
        if (a == null) return;

        // Ignore if sign loc doesn't match arena loc
        if (!a.getSign().getLocation().equals(event.getBlock().getLocation())) return;

        // Remove sign from arena
        a.setSign(null);
        MessageHandler.info(event.getPlayer(), "Removed sign for " + a.get());
    }
}
