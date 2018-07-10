package com.johncorby.arenaapi.arena;

import org.bukkit.entity.Player;

public abstract class ArenaEvents {
    public abstract void onOpen(Arena arena);

    public abstract void onRunning(Arena arena);

    public abstract void onStopped(Arena arena);

    public abstract void onJoin(Arena arena, Player player);

    public abstract void onLeave(Arena arena, Player player);
}
