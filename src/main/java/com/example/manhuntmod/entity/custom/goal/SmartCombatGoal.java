package com.example.manhuntmod.entity.custom.goal;

import com.example.manhuntmod.entity.custom.HunterEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class SmartCombatGoal extends MeleeAttackGoal {

    private final HunterEntity hunter;

    public SmartCombatGoal(HunterEntity hunter) {
        super(hunter, 1.2D, true); // speed, memory
        this.hunter = hunter;
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity target, double distance) {
        double reach = this.getAttackReachSqr(target);
        if (distance <= reach) {
            hunter.swing(InteractionHand.MAIN_HAND); // swing arm

            boolean success = hunter.doHurtTarget(target); // trigger damage
            if (hunter.getTargetPlayer() != null && hunter.getTargetPlayer().isAlive()) {
                if (hunter.debugMode)
                    System.out.println("[SmartCombat] Hit attempt: " + (success ? "SUCCESS" : "FAIL") +
                            " on " + target.getName().getString());
            }
        }
    }

    @Override
    protected double getAttackReachSqr(LivingEntity target) {
        return 4.0D; // ~2 blocks reach
    }
}
