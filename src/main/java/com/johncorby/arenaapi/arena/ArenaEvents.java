package com.johncorby.arenaapi.arena;

import org.bukkit.entity.Player;

public interface ArenaEvents {
    void onOpen(Arena arena);

    void onRunning(Arena arena);

    void onStopped(Arena arena);

    void onJoin(Arena arena, Player player);

    void onLeave(Arena arena, Player player);
}
