package com.example.manhuntmod.entity.custom.goal;

import com.example.manhuntmod.entity.custom.HunterEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Blocks;

import java.util.EnumSet;

public class SmartFarmManagerGoal extends Goal {
    private final HunterEntity hunter;

    public SmartFarmManagerGoal(HunterEntity hunter) {
        this.hunter = hunter;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return needsResources();
    }

    @Override
    public void tick() {
        if (hunter.getPlanks() < 4) {
            // Will trigger MineTreesGoal automatically
        } else if (hunter.getStoneCount() < 8 && hunter.getToolTier() < 2) {
            // Will trigger MineStoneGoal
        } else if (hunter.getVirtualInventoryCount(Blocks.IRON_ORE) < 2) {
            // Will trigger MineOresGoal
        } else if (hunter.getHunterHunger() < 10.0f) {
            // Will trigger HuntAnimalsGoal
        }
    }

    @Override
    public boolean canContinueToUse() {
        return needsResources();
    }

    private boolean needsResources() {
        return hunter.getPlanks() < 4
            || hunter.getStoneCount() < 8
            || hunter.getVirtualInventoryCount(Blocks.IRON_ORE) < 2
            || hunter.getHunterHunger() < 10.0f;
    }
}
