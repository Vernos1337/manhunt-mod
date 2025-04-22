package com.example.manhuntmod.command;

import com.example.manhuntmod.entity.ModEntities;
import com.example.manhuntmod.entity.custom.HunterEntity;
import com.example.manhuntmod.util.ModGameRules;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.event.RegisterCommandsEvent;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class ManhuntCommand {

    private static final List<ServerPlayer> activeTargets = new ArrayList<>();
    private static final List<HunterEntity> spawnedHunters = new ArrayList<>();

    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(
            Commands.literal("manhunt")
                .then(Commands.literal("start")
                    .then(Commands.argument("players", net.minecraft.commands.arguments.EntityArgument.players())
                    .then(Commands.argument("count", net.minecraft.commands.arguments.IntegerArgumentType.integer(1))
                        .executes(ctx -> {
                            List<ServerPlayer> players = new ArrayList<>(net.minecraft.commands.arguments.EntityArgument.getPlayers(ctx, "players"));
                            int count = net.minecraft.commands.arguments.IntegerArgumentType.getInteger(ctx, "count");
                            startManhunt(ctx.getSource(), players, count);
                            return 1;
                        }))))
                .then(Commands.literal("stop")
                    .executes(ctx -> {
                        stopManhunt(ctx.getSource());
                        return 1;
                    }))
        );
    }

    public static void startManhunt(CommandSourceStack source, List<ServerPlayer> targets, int hunterCount) {
        ServerLevel level = source.getLevel();
        MinecraftServer server = source.getServer();

        if (targets.isEmpty()) {
            source.sendFailure(Component.literal("§cNo target players specified."));
            return;
        }

        activeTargets.clear();
        activeTargets.addAll(targets);

        spawnedHunters.forEach(LivingEntity::kill);
        spawnedHunters.clear();

        for (int i = 0; i < hunterCount; i++) {
            ServerPlayer target = targets.get(i % targets.size());
            HunterEntity hunter = ModEntities.HUNTER.get().create(level);
            if (hunter != null) {
                hunter.setTargetPlayer(target);
                hunter.moveTo(target.getX() + 2, target.getY(), target.getZ() + 2, 0, 0);
                level.addFreshEntity(hunter);
                spawnedHunters.add(hunter);
            }
        }

        level.getGameRules().getRule(ModGameRules.MANHUNT_ACTIVE).set(true, server);
        source.sendSuccess(() ->
            Component.literal("§aStarted manhunt on " +
                targets.stream().map(p -> p.getName().getString()).collect(Collectors.joining(", ")) +
                " with " + hunterCount + " hunters."),
            false
        );
    }

    public static void stopManhunt(CommandSourceStack source) {
        spawnedHunters.forEach(LivingEntity::kill);
        spawnedHunters.clear();
        activeTargets.clear();

        ServerLevel level = source.getLevel();
        level.getGameRules().getRule(ModGameRules.MANHUNT_ACTIVE).set(false, source.getServer());

        source.sendSuccess(() -> Component.literal("§cManhunt stopped. All hunters removed."), false);
    }

    public static void checkManhuntVictory(ServerLevel level) {
        List<ServerPlayer> aliveTargets = activeTargets.stream().filter(ServerPlayer::isAlive).toList();
        if (aliveTargets.isEmpty()) {
            level.getServer().getPlayerList().broadcastSystemMessage(Component.literal("§4Hunters win! All players are dead."), false);
            for (HunterEntity h : spawnedHunters) h.kill();
            spawnedHunters.clear();
            activeTargets.clear();
            level.getGameRules().getRule(ModGameRules.MANHUNT_ACTIVE).set(false, level.getServer());
        }
    }
}
