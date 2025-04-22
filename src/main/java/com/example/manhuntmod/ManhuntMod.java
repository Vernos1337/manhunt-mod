package com.example.manhuntmod;

import com.example.manhuntmod.command.ManhuntCommand;
import com.example.manhuntmod.entity.ModEntities;
import com.example.manhuntmod.util.ModGameRules;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ManhuntMod.MODID)
public class ManhuntMod {
    public static final String MODID = "manhuntmod";

    public ManhuntMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModEntities.register(); // Register entity types

        MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);
        modEventBus.addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event) {
        GameRules.register(
            ModGameRules.MANHUNT_ACTIVE.getId(),
            GameRules.Category.PLAYER,
            ModGameRules.MANHUNT_ACTIVE.getType()
        );
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        ManhuntCommand.register(event.getDispatcher());
    }
}
