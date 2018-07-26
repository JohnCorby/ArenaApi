package com.johncorby.arenaapi.arena;

import com.boydti.fawe.object.schematic.Schematic;
import com.boydti.fawe.util.TaskManager;
import com.johncorby.arenaapi.command.Lobby;
import com.johncorby.coreapi.util.Common;
import com.johncorby.coreapi.util.MessageHandler;
import com.johncorby.coreapi.util.storedclass.ConfigIdent;
import com.johncorby.coreapi.util.storedclass.Identifiable;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BlockType;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.johncorby.arenaapi.ArenaApiPlugin.WORLD;
import static com.johncorby.arenaapi.ArenaApiPlugin.getOverridePlayers;
import static com.johncorby.coreapi.CoreApiPlugin.PLUGIN;
import static com.johncorby.coreapi.util.Common.*;
import static org.apache.commons.lang.exception.ExceptionUtils.getStackTrace;

public class Arena extends ConfigIdent<String> {
    public static ArenaEvents ARENA_EVENTS;
    private State state = State.STOPPED;

    private Sign sign;
    private Integer[] region;

    public Arena(String identity) {
        super(identity.toLowerCase());
        create();
    }

    public Arena(Map<String, Object> map) {
        super(map);
    }

    public static Arena get(String identity) {
        return get(Arena.class, identity);
    }

    public static Set<Arena> getArenas() {
        return getClasses().get(Arena.class);
    }

    // Get arena names
    public static Set<String> getNames() {
        return toSet(map(getArenas(), Identifiable::get));
    }

    // Get arena entity is in
    public static Arena arenaIn(Entity entity) {
        if (entity == null || entity.getWorld() != WORLD) return null;
        for (Arena a : getArenas())
            if (a.contains(entity)) return a;
        return null;
    }

    @Override
    public boolean dispose() {
        if (!super.dispose()) return false;
        configRemove();

        new File(PLUGIN.getDataFolder() + "/" + get() + ".schematic").delete();

        return true;
    }

    public Set<Entity> getEntities() {
        return toSet(filter(WORLD.getEntities(), this::contains));
    }

    public Set<Player> getPlayers() {
        return toSet(map(filter(getEntities(), e -> e instanceof Player), e -> (Player) e));
    }

    public boolean contains(Entity entity) {
        if (entity == null) return false;
        Integer[] l = {entity.getLocation().getBlockX(), entity.getLocation().getBlockZ()};
        return state != State.STOPPED &&
                entity.getWorld() == WORLD &&
                l[0] >= region[0] &&
                l[0] <= region[2] &&
                l[1] >= region[1] &&
                l[1] <= region[3];
    }

    // Message to players in arena
    public void broadcast(Object message) {
        broadcast(message, (Player) null);
    }

    // Message to players in arena except
    public void broadcast(Object message, Player... except) {
        Set<Player> es = toSet(except);
        for (Player p : getPlayers())
            if (!es.contains(p))
                MessageHandler.info(p, message);
    }


    public State getState() {
        return state;
    }

    public void setState(State state) {
        if (this.state == state) return;
        switch (state) {
            case OPEN:
                ARENA_EVENTS.onOpen(this);
                break;
            case RUNNING:
                broadcast("Game start");

                ARENA_EVENTS.onRunning(this);
                break;
            case STOPPED:
                // Remove entities
                for (Entity e : getEntities())
                    remove(e);

                setBlocks();

                ARENA_EVENTS.onStopped(this);
                break;
        }
        this.state = state;

        updateSign();
    }

    public Integer[] getRegion() {
        return region;
    }

    public void setRegion(Integer[] region) {
        if (region == null) return;

        this.region = region;
        configAdd();

        getBlocks();
    }


    public Sign getSign() {
        return sign;
    }

    public void setSign(Sign sign) {
        if (sign == null) return;

        this.sign = sign;
        updateSign();
        configAdd();
    }

    public boolean add(Entity entity) {
        Arena aI = arenaIn(entity);

        // If entity is player
        if (entity instanceof Player) {
            Player p = (Player) entity;

            // Error if arena has started
            if (state == State.RUNNING && !getOverridePlayers().contains(p)) {
                MessageHandler.error(p, "Arena " + get() + " isn't joinable");
                return false;
            }

            // Don't join arena if in it
            if (aI == this && !getOverridePlayers().contains(p)) {
                MessageHandler.error(p, "You're already in arena " + get());
                return false;
            }

            // Leave arena if in one
            if (aI != null) aI.remove(p);

            // Teleport to random loc
            tpToRandom(p);

            // Start arena if it's stopped
            if (state == State.STOPPED) setState(State.OPEN);

            // Send join messages
            MessageHandler.info(p, "Joined arena " + get());
            broadcast(p.getName() + " joined the arena", p);

            updateSign();

            ARENA_EVENTS.onJoin(this, p);
        } else {
            // Don't join arena if in it
            if (aI == this) return false;
        }

        //return getEntities().add(entity);
        return contains(entity);
    }

    public boolean remove(Entity entity) {
        //boolean result = getEntities().dispose(entity);

        // If entity is player
        if (entity instanceof Player) {
            Player p = (Player) entity;

            // Teleport to lobby
            Lobby.lobby(p);

            // Stop arena if no more players
            if (getPlayers().size() == 0) setState(State.STOPPED);

            // Stop arena if running and less than 2 players
            if (state == State.RUNNING && getPlayers().size() < 2) setState(State.STOPPED);

            // Send leave messages
            broadcast(p.getName() + " left the arena", p);
            MessageHandler.info(p, "Left arena " + get());

            updateSign();

            ARENA_EVENTS.onLeave(this, p);
        } else entity.remove();

        return !contains(entity);
    }

    // Update sign
    private void updateSign() {
        if (sign == null) return;
        sign.setLine(0, MessageHandler.prefix);
        sign.setLine(1, get());
        sign.setLine(2, state.get());
        sign.setLine(3, toStr(getPlayers().size()));
        sign.update(true, false);
    }

    // Get blocks
    private void getBlocks() {
        //state = State.UPDATING;
        //updateSign();

        TaskManager.IMP.async(() -> {
            long time = Common.time(() -> {
                try {
                    // Remove entities so they aren't saved in the schematic
                    // Except not players because that causes issues
                    state = State.OPEN;
                    for (Entity e : getEntities())
                        if (!(e instanceof Player)) e.remove();
                    state = State.STOPPED;

                    File file = new File(PLUGIN.getDataFolder() + "/" + get() + ".schematic");
                    World world = new BukkitWorld(WORLD);
                    Vector pos1 = new Vector(region[0], 0, region[1]);
                    Vector pos2 = new Vector(region[2], 255, region[3]);
                    CuboidRegion region = new CuboidRegion(world, pos1, pos2);
                    new Schematic(region).save(file, ClipboardFormat.SCHEMATIC);
                } catch (Exception e) {
                    error(getStackTrace(e));
                }
            });

            //state = State.STOPPED;
            //updateSign();
            debug("Got blocks async in " + time + " ms");
        });
    }

    // Set blocks
    private void setBlocks() {
        //state = State.UPDATING;
        //updateSign();

        TaskManager.IMP.async(() -> {
            long time = Common.time(() -> {
                try {
                    File file = new File(PLUGIN.getDataFolder() + "/" + get() + ".schematic");
                    World world = new BukkitWorld(WORLD);
                    Vector pos1 = new Vector(region[0], 0, region[1]);
                    //ClipboardFormat.SCHEMATIC.load(file).paste(world, pos1, false, true, null);
                    Clipboard clipboard = ClipboardFormat.SCHEMATIC.getReader(new FileInputStream(file)).read(world.getWorldData());
                    new Schematic(clipboard).paste(world, pos1, false, true, null);
                } catch (Exception e) {
                    error(getStackTrace(e));
                }
            });

            //state = State.STOPPED;
            //updateSign();
            debug("Set blocks async in " + time + " ms");
        });
    }

    // Edited from FAWE
    public void tpToRandom(Player player) {
        Location l = new Location(WORLD,
                randInt(region[0], region[2]),
                randInt(0, 250),
                randInt(region[1], region[3]),
                randInt(-180, 180),
                0);

        int x = l.getBlockX();
        int y = l.getBlockY();
        int z = l.getBlockZ();

        // Tp to unstuck
        int origY = y;

        byte free = 0;

        while (y <= WORLD.getMaxHeight() + 2) {
            Block b = WORLD.getBlockAt(x, y, z);
            if (BlockType.canPassThrough(b.getTypeId(), b.getData())) {
                ++free;
            } else {
                free = 0;
            }

            if (free == 2) {
                if (y - 1 != origY) {
                    b = WORLD.getBlockAt(x, y - 2, z);
                    l.setX(x + .5);
                    l.setY(y - 2 + BlockType.centralTopLimit(b.getTypeId(), b.getData()));
                    l.setZ(z + .5);
                }

                break;
            }

            ++y;
        }

        // Tp to ground
        while (y >= 0) {
            Block b = WORLD.getBlockAt(x, y, z);
            final int id = b.getTypeId();
            final int data = b.getData();
            if (!BlockType.canPassThrough(b.getTypeId(), b.getData())) {
                l.setX(x + .5);
                l.setY(y + BlockType.centralTopLimit(id, data));
                l.setZ(z + .5);
                break;
            }

            --y;
        }

        player.teleport(l);
    }


    @Override
    public List<String> getDebug() {
        List<String> r = super.getDebug();

        r.add("State: " + state.get());

        Location s = sign == null ? null : sign.getLocation();
        r.add("Region: " +
                (region == null ? "N/A" : String.format("(%s, %s), (%s, %s)", region[0], region[1], region[2], region[3])));
        r.add("SignLoc: " +
                (s == null ? "N/A" : String.format("(%s, %s, %s)", s.getBlockX(), s.getBlockY(), s.getBlockZ())));

        Set<Entity> el = getEntities();
        r.add("Entities: " + (el.isEmpty() ? "None" : ""));
        for (Entity e : el) {
            Location l = e.getLocation();
            r.add(String.format("%s {(%s, %s, %s), (%s, %s)}",
                    e.getType(),
                    l.getBlockX(),
                    l.getBlockY(),
                    l.getBlockZ(),
                    (int) l.getPitch(),
                    (int) l.getYaw()));
        }

        return r;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        map.put("Region", region);
        map.put("SignLoc", sign.getLocation());
        return map;
    }

    @Override
    public void deserialize(Map<String, Object> map) {
        super.deserialize(map);
        region = (Integer[]) map.get("Region");
        Object o = map.get("SignLoc");
        if (o instanceof Location) sign = (Sign) ((Location) o).getBlock().getState();
        create();
    }

    @Override
    public void configAdd() {
        Set<Arena> set = toSet((Collection<Arena>) PLUGIN.getConfig().getList("Arenas"));
        set.remove(this);
        set.add(this);
        PLUGIN.getConfig().set("Arenas", toList(set));
        PLUGIN.saveConfig();
    }

    @Override
    public void configRemove() {
        Set<Arena> set = toSet((Collection<Arena>) PLUGIN.getConfig().getList("Arenas"));
        set.remove(this);
        PLUGIN.getConfig().set("Arenas", toList(set));
        PLUGIN.saveConfig();
    }

    public enum State {
        OPEN(true),
        RUNNING(false),
        STOPPED(true);

        private final boolean joinable;

        State(boolean joinable) {
            this.joinable = joinable;
        }

        public String get() {
            return (joinable ? ChatColor.GREEN : ChatColor.RED) + name();
        }
    }
}
