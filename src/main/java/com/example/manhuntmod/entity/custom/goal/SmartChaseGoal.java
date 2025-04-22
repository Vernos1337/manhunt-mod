package com.example.manhuntmod.entity.custom.goal;

import com.example.manhuntmod.entity.custom.HunterEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class SmartChaseGoal extends Goal {

    private final HunterEntity hunter;

    public SmartChaseGoal(HunterEntity hunter) {
        this.hunter = hunter;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        ServerPlayer target = hunter.getTargetPlayer();
        return target != null && target.isAlive();
    }

    @Override
    public void tick() {
        ServerPlayer target = hunter.getTargetPlayer();
        if (target == null || !target.isAlive()) return;

        double distance = hunter.distanceTo(target);
        if (hunter.debugMode && hunter.tickCount % 40 == 0) {
            System.out.printf("[SmartChase] Chasing %s | Distance: %.2f%n", target.getName().getString(), distance);
        }

        // Move towards player, ignore line of sight
        hunter.getNavigation().moveTo(target, 1.3D);
    }
}
