package com.example.manhuntmod.entity.custom.goal;

import com.example.manhuntmod.entity.custom.HunterEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Blocks;

import java.util.EnumSet;

public class BridgeIfStuckGoal extends Goal {
    private final HunterEntity hunter;
    private int stuckTicks = 0;

    public BridgeIfStuckGoal(HunterEntity hunter) {
        this.hunter = hunter;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return hunter.getNavigation().isStuck();
    }

    @Override
    public void start() {
        stuckTicks = 0;
    }

    @Override
    public void tick() {
        stuckTicks++;
        if (stuckTicks > 20) {
            BlockPos below = hunter.blockPosition().below();
            BlockPos current = hunter.blockPosition();

            if (hunter.getPlanks() > 0 && hunter.level().isEmptyBlock(below)) {
                hunter.level().setBlockAndUpdate(below, Blocks.OAK_PLANKS.defaultBlockState());
                hunter.usePlanks(1);
            } else if (hunter.getStoneCount() > 0 && hunter.level().isEmptyBlock(below)) {
                hunter.level().setBlockAndUpdate(below, Blocks.COBBLESTONE.defaultBlockState());
            }
        }
    }
}
