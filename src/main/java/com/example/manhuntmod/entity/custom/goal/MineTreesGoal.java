package com.example.manhuntmod.entity.custom.goal;

import com.example.manhuntmod.entity.custom.HunterEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.EnumSet;

public class MineTreesGoal extends Goal {
    private final HunterEntity hunter;
    private BlockPos targetLogPos;
    private int miningTimer = 0;

    public MineTreesGoal(HunterEntity hunter) {
        this.hunter = hunter;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return findNearbyLog() != null;
    }

    @Override
    public void start() {
        targetLogPos = findNearbyLog();
        if (targetLogPos != null) {
            hunter.getNavigation().moveTo(targetLogPos.getX(), targetLogPos.getY(), targetLogPos.getZ(), 1.0);
        }
    }

    @Override
    public void tick() {
        if (targetLogPos == null) return;

        double dist = hunter.distanceToSqr(targetLogPos.getX(), targetLogPos.getY(), targetLogPos.getZ());
        if (dist < 2.5) {
            miningTimer++;
            if (miningTimer > 40) {
                hunter.level().destroyBlock(targetLogPos, false);
                hunter.addWoodLog(1);
                hunter.tryCrafting();
                miningTimer = 0;
                targetLogPos = null;
            }
        } else {
            hunter.getNavigation().moveTo(targetLogPos.getX(), targetLogPos.getY(), targetLogPos.getZ(), 1.0);
        }
    }

    private BlockPos findNearbyLog() {
        BlockPos origin = hunter.blockPosition();
        Level level = hunter.level();
        int radius = 10;

        for (int y = -2; y <= 3; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos check = origin.offset(x, y, z);
                    if (level.getBlockState(check).getBlock() == Blocks.OAK_LOG) {
                        return check;
                    }
                }
            }
        }
        return null;
    }
}
