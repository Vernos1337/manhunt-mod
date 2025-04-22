package com.example.manhuntmod.entity.custom.goal;

import com.example.manhuntmod.entity.custom.HunterEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.Level;

import java.util.EnumSet;

public class CombatStrategyGoal extends Goal {
    private final HunterEntity hunter;
    private int tickCounter = 0;

    public CombatStrategyGoal(HunterEntity hunter) {
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

        double dist = hunter.distanceTo(target);
        tickCounter++;

        // 🩹 Retreat if weak
        if (hunter.getHealth() < 6.0F) {
            double backX = hunter.getX() + (hunter.getX() - target.getX());
            double backZ = hunter.getZ() + (hunter.getZ() - target.getZ());
            hunter.getNavigation().moveTo(backX, hunter.getY(), backZ, 1.3D);
            return;
        }

        // 🏹 Ranged attack if far
        if (dist > 12.0D && tickCounter % 40 == 0) {
            shootArrow(target);
        }

        // 🕺 Strafing and jumping
        if (dist <= 8.0D) {
            double strafeX = Math.sin(tickCounter * 0.3) * 2;
            double strafeZ = Math.cos(tickCounter * 0.3) * 2;
            hunter.getNavigation().moveTo(target.getX() + strafeX, target.getY(), target.getZ() + strafeZ, 1.2D);

            // Jump occasionally
            if (tickCounter % 20 == 0) {
                hunter.setJumping(true);
            }
        }

        // ☠️ Critical hit simulation
        if (dist <= 3.0 && tickCounter % 30 == 0) {
            hunter.setJumping(true); // mimic jump

            if (!hunter.onGround()) {
                float base = (float) hunter.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE).getValue();
                float crit = base * 1.5F;
                target.hurt(hunter.damageSources().mobAttack(hunter), crit);
            }
        }

        hunter.getLookControl().setLookAt(target, 30.0F, 30.0F);
    }

    private void shootArrow(ServerPlayer target) {
        Level level = hunter.level();
        Arrow arrow = new Arrow(level, hunter);
        arrow.setOwner(hunter);
        arrow.setPos(hunter.getX(), hunter.getEyeY() - 0.1, hunter.getZ());
        arrow.shootFromRotation(hunter, hunter.getXRot(), hunter.getYRot(), 0.0F, 1.6F, 1.0F);
        level.addFreshEntity(arrow);
        hunter.swing(hunter.getUsedItemHand(), true);
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }
}
