package com.example.manhuntmod.entity.custom.goal;

import com.example.manhuntmod.entity.custom.HunterEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class TrackPlayerGoal extends Goal {
    private final HunterEntity hunter;

    public TrackPlayerGoal(HunterEntity hunter) {
        this.hunter = hunter;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return hunter.getTargetPlayer() != null && hunter.getTargetPlayer().isAlive();
    }

    @Override
    public void tick() {
        ServerPlayer target = hunter.getTargetPlayer();
        if (target == null || !target.isAlive()) return;

        double speed = hunter.getHunterHunger() >= 6.0f ? 1.4D : 1.0D;

        hunter.getNavigation().moveTo(target.getX(), target.getY(), target.getZ(), speed);
        hunter.getLookControl().setLookAt(target, 30.0F, 30.0F);
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }
}
