package com.johncorby.arenaapi.arena;

import com.johncorby.coreapi.util.MessageHandler;
import com.johncorby.coreapi.util.storedclass.Identifiable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SetRegion extends Identifiable<Player> {
    public final Integer[] region = new Integer[4];
    public int step;
    public String name;
    private boolean add;

    public SetRegion(@NotNull Player identity, @NotNull String name, boolean add) {
        super(identity);
        create(identity, name, add);
    }

    @Nullable
    public static SetRegion get(Player identity) {
        return get(SetRegion.class, identity);
    }

    protected boolean create(@NotNull Player identity, @NotNull String name, boolean add) throws IllegalStateException {
        // Error if arena doesn't exist
        if (Arena.get(name) == null && !add) {
            MessageHandler.error(identity, "Arena " + name + " does not exist");
            return false;
        }

        // Error if player already setting region
        SetRegion sR = get(identity);
        if (sR != null) {
            MessageHandler.error(identity, "You are already setting region for arena " + sR.name);
            return false;
        }

        MessageHandler.info(identity, "Left click block to set pos 1");

        this.name = name.toLowerCase();
        this.add = add;
        return super.create(identity);
    }

    @Override
    protected boolean create(Player identity) {
        return true;
    }

    // Go to next step in region setting
    public void next(int x, int z) {
        switch (step) {
            case 0: // Set first pos
                region[0] = x;
                region[1] = z;
                MessageHandler.info(get(), "Left click block to set pos 2");
                break;
            case 1: // Set second pos
                region[2] = x;
                region[3] = z;
                // Swap so 1st pos is min and 2nd is max
                if (region[0] > region[2]) {
                    Integer temp = region[0];
                    region[0] = region[2];
                    region[2] = temp;
                }
                if (region[1] > region[3]) {
                    Integer temp = region[1];
                    region[1] = region[3];
                    region[3] = temp;
                }

                // Add of update arena region
                if (add) {
                    Arena arena = new Arena(name);
                    arena.setRegion(region);
                } else Arena.get(name).setRegion(region);

                MessageHandler.info(get(), "Arena " + name + " region set");
                dispose();
                break;
        }
        step++;
    }
}



