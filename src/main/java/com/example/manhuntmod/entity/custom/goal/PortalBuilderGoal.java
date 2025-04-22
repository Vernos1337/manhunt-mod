package com.example.manhuntmod.entity.custom.goal;

import com.example.manhuntmod.entity.custom.HunterEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Blocks;

import java.util.EnumSet;

public class PortalBuilderGoal extends Goal {
    private final HunterEntity hunter;
    private boolean portalBuilt = false;

    public PortalBuilderGoal(HunterEntity hunter) {
        this.hunter = hunter;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return hunter.getTargetPlayer() != null &&
               hunter.getTargetPlayer().level().dimension() != hunter.level().dimension();
    }

    @Override
    public void start() {
        if (!portalBuilt) {
            BlockPos pos = hunter.blockPosition().offset(0, 0, 1);
            // For simplicity we just place a "fake" portal
            for (int y = 0; y < 3; y++) {
                hunter.level().setBlockAndUpdate(pos.above(y), Blocks.OBSIDIAN.defaultBlockState());
            }
            hunter.level().setBlockAndUpdate(pos.above(1), Blocks.NETHER_PORTAL.defaultBlockState());
            portalBuilt = true;
        }
    }
}
