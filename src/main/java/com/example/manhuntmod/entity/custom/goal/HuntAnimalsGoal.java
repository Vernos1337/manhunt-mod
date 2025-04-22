package com.example.manhuntmod.entity.custom.goal;

import com.example.manhuntmod.entity.custom.HunterEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.EnumSet;
import java.util.List;

public class HuntAnimalsGoal extends Goal {
    private final HunterEntity hunter;
    private Animal targetAnimal;
    private int cooldown = 0;

    public HuntAnimalsGoal(HunterEntity hunter) {
        this.hunter = hunter;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return (hunter.getHealth() < hunter.getMaxHealth() || hunter.getHunterHunger() < 16.0F)
               && findNearbyAnimal() != null;
    }

    @Override
    public void start() {
        targetAnimal = findNearbyAnimal();
        if (targetAnimal != null) {
            hunter.getNavigation().moveTo(targetAnimal, 1.3D); // Sprint to food
        }
    }

    @Override
    public void tick() {
        if (targetAnimal == null || !targetAnimal.isAlive()) return;

        hunter.getLookControl().setLookAt(targetAnimal, 30F, 30F);
        hunter.getNavigation().moveTo(targetAnimal, 1.3D);

        if (hunter.distanceTo(targetAnimal) < 2.0D && cooldown == 0) {
            targetAnimal.kill();
            hunter.feed(6.0F); // Restores hunger + health
            cooldown = 40;
        }

        if (cooldown > 0) cooldown--;
    }

    @Override
    public boolean canContinueToUse() {
        return targetAnimal != null && targetAnimal.isAlive() &&
               (hunter.getHealth() < hunter.getMaxHealth() || hunter.getHunterHunger() < 20.0F);
    }

    private Animal findNearbyAnimal() {
        Level level = hunter.level();
        AABB range = hunter.getBoundingBox().inflate(20);
        List<Animal> animals = level.getEntitiesOfClass(Animal.class, range, a -> a.isAlive());
        return animals.isEmpty() ? null : animals.get(0);
    }
}
