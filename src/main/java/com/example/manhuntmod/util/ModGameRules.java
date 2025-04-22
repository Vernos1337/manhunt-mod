package com.example.manhuntmod.util;

import net.minecraft.world.level.GameRules;

public class ModGameRules {
    public static final GameRules.Key<GameRules.BooleanValue> MANHUNT_ACTIVE =
        GameRules.register("manhuntActive", GameRules.Category.PLAYER, GameRules.BooleanValue.create(false));
}
