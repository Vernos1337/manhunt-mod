package com.example.manhuntmod.entity;

import com.example.manhuntmod.ManhuntMod;
import com.example.manhuntmod.entity.custom.HunterEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod.EventBusSubscriber(modid = ManhuntMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
        DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ManhuntMod.MODID);

    public static final RegistryObject<EntityType<HunterEntity>> HUNTER =
        ENTITY_TYPES.register("hunter", () ->
            EntityType.Builder.of(HunterEntity::new, MobCategory.MONSTER)
                .sized(0.6f, 1.95f)
                .build("hunter"));

    public static void register() {
        ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
