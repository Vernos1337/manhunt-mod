package com.example.manhuntmod.entity.custom.goal;

import com.example.manhuntmod.entity.custom.HunterEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.EnumSet;

public class MineStoneGoal extends Goal {
    private final HunterEntity hunter;
    private BlockPos targetStonePos;
    private int miningTimer = 0;

    public MineStoneGoal(HunterEntity hunter) {
        this.hunter = hunter;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return hunter.getToolTier() == 1 && hunter.getStoneCount() < 8;
    }

    @Override
    public void start() {
        targetStonePos = findNearbyStone();
        if (targetStonePos != null) {
            hunter.getNavigation().moveTo(targetStonePos.getX(), targetStonePos.getY(), targetStonePos.getZ(), 1.0);
        }
    }

    @Override
    public void tick() {
        if (targetStonePos == null) return;

        double dist = hunter.distanceToSqr(targetStonePos.getX(), targetStonePos.getY(), targetStonePos.getZ());
        if (dist < 2.5) {
            miningTimer++;
            if (miningTimer > 40) {
                hunter.level().destroyBlock(targetStonePos, false);
                hunter.addStone(1);
                hunter.tryCraftStoneTools();
                miningTimer = 0;
                targetStonePos = null;
            }
        } else {
            hunter.getNavigation().moveTo(targetStonePos.getX(), targetStonePos.getY(), targetStonePos.getZ(), 1.0);
        }
    }

    private BlockPos findNearbyStone() {
        BlockPos origin = hunter.blockPosition();
        Level level = hunter.level();
        int radius = 10;

        for (int y = -2; y <= 2; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos check = origin.offset(x, y, z);
                    if (level.getBlockState(check).getBlock() == Blocks.STONE || 
                        level.getBlockState(check).getBlock() == Blocks.COBBLESTONE) {
                        return check;
                    }
                }
            }
        }
        return null;
    }
}
