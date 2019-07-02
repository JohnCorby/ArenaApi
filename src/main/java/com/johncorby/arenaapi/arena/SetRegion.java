package com.johncorby.arenaapi.arena;

import com.johncorby.coreapi.util.MessageHandler;
import com.johncorby.coreapi.util.eventconversation.EventConversation;
import com.johncorby.coreapi.util.eventconversation.EventPrompt;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SetRegion extends EventConversation {
    public final Integer[] region = new Integer[4];
    @NotNull
    public final String name;
    private final boolean add;

    public SetRegion(Player forWhom, String name, boolean add) {
        super(forWhom);
        this.name = name.toLowerCase();
        this.add = add;
        setFirstPrompt(new EventPrompt<BlockBreakEvent>(BlockBreakEvent.class,
                this,
                BlockBreakEvent::getPlayer) {
            @NotNull
            @Override
            protected String getPromptText() {
                return "Left click block to setSet pos 1";
            }

            @Override
            protected boolean isInputValid(BlockBreakEvent input) {
                return true;
            }

            @Nullable
            @Override
            protected String getInvalidInputText(BlockBreakEvent input) {
                return null;
            }

            @Nullable
            @Override
            protected EventPrompt acceptValidInput(@NotNull BlockBreakEvent input) {
                input.setCancelled(true);

                Location l = input.getBlock().getLocation();
                region[0] = l.getBlockX();
                region[1] = l.getBlockZ();

                return new EventPrompt<BlockBreakEvent>(BlockBreakEvent.class,
                        SetRegion.this,
                        BlockBreakEvent::getPlayer) {
                    @NotNull
                    @Override
                    protected String getPromptText() {
                        return "Left click block to setSet pos 2";
                    }

                    @Override
                    protected boolean isInputValid(BlockBreakEvent input) {
                        return true;
                    }

                    @Nullable
                    @Override
                    protected String getInvalidInputText(BlockBreakEvent input) {
                        return null;
                    }

                    @Nullable
                    @Override
                    protected EventPrompt acceptValidInput(@NotNull BlockBreakEvent input) {
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

    @Nullable
    public static SetRegion get(Player identity) {
        return get(SetRegion.class, identity);
    }

    @Override
    public boolean create() {
        // Error if already setting arena
        if (exists()) {
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
