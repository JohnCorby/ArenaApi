package com.johncorby.arenaapi.arena;

import com.johncorby.coreapi.util.MessageHandler;
import com.johncorby.coreapi.util.eventconversation.EventConversation;
import com.johncorby.coreapi.util.eventconversation.EventPrompt;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class SetRegion extends EventConversation {
    public final Integer[] region = new Integer[4];
    public final String name;
    private final boolean add;

    public SetRegion(Player forWhom, String name, boolean add) {
        super(forWhom);
        this.name = name.toLowerCase();
        this.add = add;
        setFirstPrompt(new EventPrompt<BlockBreakEvent>(BlockBreakEvent.class,
                this,
                BlockBreakEvent::getPlayer) {
            @Override
            protected String getPromptText() {
                return "Left click block to set pos 1";
            }

            @Override
            protected boolean isInputValid(BlockBreakEvent input) {
                return true;
            }

            @Override
            protected String getInvalidInputText(BlockBreakEvent input) {
                return null;
            }

            @Override
            protected EventPrompt acceptValidInput(BlockBreakEvent input) {
                input.setCancelled(true);

                Location l = input.getBlock().getLocation();
                region[0] = l.getBlockX();
                region[1] = l.getBlockZ();

                return new EventPrompt<BlockBreakEvent>(BlockBreakEvent.class,
                        SetRegion.this,
                        BlockBreakEvent::getPlayer) {
                    @Override
                    protected String getPromptText() {
                        return "Left click block to set pos 2";
                    }

                    @Override
                    protected boolean isInputValid(BlockBreakEvent input) {
                        return true;
                    }

                    @Override
                    protected String getInvalidInputText(BlockBreakEvent input) {
                        return null;
                    }

                    @Override
                    protected EventPrompt acceptValidInput(BlockBreakEvent input) {
                        input.setCancelled(true);

                        Location l = input.getBlock().getLocation();
                        region[2] = l.getBlockX();
                        region[3] = l.getBlockZ();

                        // Swap so 1st pos is min and 2nd is max
                        Integer temp;
                        if (region[0] > region[2]) {
                            temp = region[0];
                            region[0] = region[2];
                            region[2] = temp;
                        }
                        if (region[1] > region[3]) {
                            temp = region[1];
                            region[1] = region[3];
                            region[3] = temp;
                        }

                        // Add or update arena region
                        if (add) new Arena(name).setRegion(region);
                        else Arena.get(name).setRegion(region);

                        MessageHandler.info(getForWhom(), "Arena " + name + " region set");
                        return null;
                    }
                };
            }
        });
        create();
    }

    public static SetRegion get(Player identity) {
        return get(SetRegion.class, identity);
    }

    @Override
    public boolean create() throws IllegalStateException {
        // Error if already setting arena
        if (stored()) {
            MessageHandler.error(identity, "You are already setting region for arena " + name);
            return false;
        }

        // Error if arena exists and adding
        if (Arena.get(name) != null && add) {
            MessageHandler.error(identity, "Arena " + name + " already exists");
            return false;
        }

        // Error if arena doesn't exist and updating
        if (Arena.get(name) == null && !add) {
            MessageHandler.error(identity, "Arena " + name + " doesn't exist");
            return false;
        }

        return super.create();
    }
}

/*
class _SetRegion extends Identifiable<Player> {
    public final Integer[] region = new Integer[4];
    public int step;
    public String name;
    private boolean add;

    public SetRegion(Player identity, String name, boolean add) {
        super(identity);
        create(identity, name, add);
    }


    public static SetRegion get(Player identity) {
        return get(SetRegion.class, identity);
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
*/


