package com.johncorby.arenaapi.event;

import com.johncorby.arenaapi.arena.Arena;
import com.johncorby.coreapi.util.MessageHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.plugin.RegisteredListener;

import static com.johncorby.coreapi.CoreApiPlugin.PLUGIN;

public class Any implements Listener {
    Any() {
        RegisteredListener registeredListener = new RegisteredListener(this,
                (listener, event) ->
                {
                    if (event instanceof BlockEvent) {
                        BlockEvent blockEvent = (BlockEvent) event;
                        //Common.debug("Block Event: " + blockEvent.getEventName());

                        // Ignore if physics (aka block update) event
                        //if (blockEvent.getEventName().equalsIgnoreCase("BlockPhysicsEvent")) return;

                        // Ignore if not in arena
                        Arena aI = Arena.arenaIn(blockEvent.getBlock().getLocation());
                        if (aI == null) return;

                        // Add block to arena's changed blocks ArrayList
                        MessageHandler.debug("Block Event in arena: " + blockEvent.getEventName());
                        //aI.add(blockEvent.getBlock().getState());
                    } else {
                        //Common.debug("Event: " + event.getEventName());
                    }

                },
                EventPriority.NORMAL, PLUGIN, false);
        for (HandlerList handler : HandlerList.getHandlerLists())
            handler.register(registeredListener);
    }
}
