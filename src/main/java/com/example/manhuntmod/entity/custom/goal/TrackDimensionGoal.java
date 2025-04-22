package com.example.manhuntmod.entity.custom.goal;

import com.example.manhuntmod.entity.custom.HunterEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumSet;

public class TrackDimensionGoal extends Goal {
    private final HunterEntity hunter;
    private BlockPos portalPos = null;
    private int tickDelay = 0;

    public TrackDimensionGoal(HunterEntity hunter) {
        this.hunter = hunter;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return hunter.getTargetPlayer() != null &&
               !hunter.level().dimension().equals(hunter.getTargetPlayer().level().dimension());
    }

    @Override
    public void start() {
        portalPos = findNearbyPortal();
        tickDelay = 0;
        if (portalPos != null) {
            hunter.getNavigation().moveTo(portalPos.getX(), portalPos.getY(), portalPos.getZ(), 1.0);
        }
    }

    @Override
    public void tick() {
        if (portalPos == null || tickDelay++ < 40) return;

        double distance = hunter.distanceToSqr(portalPos.getX(), portalPos.getY(), portalPos.getZ());
        if (distance < 3.0) {
            if (!hunter.level().isClientSide) {
                ServerLevel targetLevel = hunter.getServer().getLevel(hunter.getTargetPlayer().level().dimension());
                if (targetLevel != null) {
                    hunter.changeDimension(targetLevel);
                }
            }
        }
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    private BlockPos findNearbyPortal() {
        BlockPos origin = hunter.blockPosition();
        int radius = 15;

        for (int y = -4; y <= 4; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos checkPos = origin.offset(x, y, z);
                    BlockState state = hunter.level().getBlockState(checkPos);
                    if (state.is(Blocks.NETHER_PORTAL)) return checkPos;
                }
            }
        }
        return null;
    }
}
