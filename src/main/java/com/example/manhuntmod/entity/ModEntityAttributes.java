package com.example.manhuntmod.entity;

import com.example.manhuntmod.entity.custom.HunterEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.example.manhuntmod.ManhuntMod.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntityAttributes {
    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        AttributeSupplier attributes = HunterEntity.createAttributes().build();
        event.put(ModEntities.HUNTER.get(), attributes);
    }
}
