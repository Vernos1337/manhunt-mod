package com.example.manhuntmod.entity.custom.goal;

import com.example.manhuntmod.entity.custom.HunterEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.EnumSet;

public class MineOresGoal extends Goal {
    private final HunterEntity hunter;
    private BlockPos targetOrePos;
    private int miningTimer = 0;

    public MineOresGoal(HunterEntity hunter) {
        this.hunter = hunter;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return findNearestOre() != null;
    }

    @Override
    public void start() {
        targetOrePos = findNearestOre();
        if (targetOrePos != null) {
            hunter.getNavigation().moveTo(targetOrePos.getX(), targetOrePos.getY(), targetOrePos.getZ(), 1.0);
        }
    }

    @Override
    public void tick() {
        if (targetOrePos == null) return;

        double dist = hunter.distanceToSqr(targetOrePos.getX(), targetOrePos.getY(), targetOrePos.getZ());
        if (dist < 2.5) {
            miningTimer++;
            if (miningTimer > 40) {
                Block block = hunter.level().getBlockState(targetOrePos).getBlock();
                hunter.level().destroyBlock(targetOrePos, false);

                if (block == Blocks.IRON_ORE) {
                    hunter.addIronOre(1);
                    hunter.trySmeltingIron();
                } else if (block == Blocks.COAL_ORE) {
                    hunter.addCoal(1);
                } else if (block == Blocks.DIAMOND_ORE) {
                    hunter.addDiamond(1);
                    hunter.craftDiamondGear();
                }

                miningTimer = 0;
                targetOrePos = null;
            }
        } else {
            hunter.getNavigation().moveTo(targetOrePos.getX(), targetOrePos.getY(), targetOrePos.getZ(), 1.0);
        }
    }

    private BlockPos findNearestOre() {
        BlockPos origin = hunter.blockPosition();
        Level level = hunter.level();
        int radius = 10;

        for (int y = -10; y <= 10; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos checkPos = origin.offset(x, y, z);
                    Block block = level.getBlockState(checkPos).getBlock();
                    if (block == Blocks.IRON_ORE || block == Blocks.COAL_ORE || block == Blocks.DIAMOND_ORE) {
                        return checkPos;
                    }
                }
            }
        }
        return null;
    }
}
