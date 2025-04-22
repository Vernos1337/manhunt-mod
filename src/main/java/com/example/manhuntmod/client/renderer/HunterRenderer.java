package com.example.manhuntmod.client.renderer;

import com.example.manhuntmod.entity.custom.HunterEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;

public class HunterRenderer extends MobRenderer<HunterEntity, HumanoidModel<HunterEntity>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("manhuntmod", "textures/entity/hunter.png");

    public HunterRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5F);

        this.addLayer(new HumanoidArmorLayer<>(
            this,
            new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
            new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)),
            context.getModelManager()
        ));
    }

    @Override
    public ResourceLocation getTextureLocation(HunterEntity entity) {
        return TEXTURE;
    }
}
