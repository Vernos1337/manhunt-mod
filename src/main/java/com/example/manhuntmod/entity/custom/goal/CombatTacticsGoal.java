package com.example.manhuntmod.entity.custom.goal;

import com.example.manhuntmod.entity.custom.HunterEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.Level;

import java.util.EnumSet;

public class CombatTacticsGoal extends Goal {
    private final HunterEntity hunter;
    private int tickCounter = 0;

    public CombatTacticsGoal(HunterEntity hunter) {
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
        if (target == null) return;

        double dist = hunter.distanceToSqr(target);
        if (hunter.getHealth() < 6.0F) {
            hunter.getNavigation().moveTo(hunter.getX() - 5, hunter.getY(), hunter.getZ() - 5, 1.0);
            return;
        }

        if (dist > 25.0D && dist < 64.0D && tickCounter % 40 == 0) {
            Level level = hunter.level();
            Arrow arrow = new Arrow(level, hunter);
            arrow.setOwner(hunter);
            arrow.setPos(hunter.getX(), hunter.getEyeY() - 0.1, hunter.getZ());
            arrow.shootFromRotation(hunter, hunter.getXRot(), hunter.getYRot(), 0.0F, 1.6F, 1.0F);
            level.addFreshEntity(arrow);
            hunter.swing(hunter.getUsedItemHand(), true);
        }

        if (dist > 5.0D) {
            double angle = Math.sin(tickCounter * 0.3);
            double offsetX = angle * 2;
            double offsetZ = Math.cos(angle) * 2;
            hunter.getNavigation().moveTo(target.getX() + offsetX, target.getY(), target.getZ() + offsetZ, 1.2);
        }

        hunter.getLookControl().setLookAt(target, 30.0F, 30.0F);
        tickCounter++;
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }
}
