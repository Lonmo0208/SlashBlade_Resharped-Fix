package mods.flammpfeil.slashblade.registry.slashblade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import mods.flammpfeil.slashblade.client.renderer.CarryType;
import mods.flammpfeil.slashblade.client.renderer.model.BladeModelManager;
import net.minecraft.resources.ResourceLocation;

public class RenderDefinition {
    public static final Codec<RenderDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("texture", BladeModelManager.resourceDefaultTexture)
                    .forGetter(RenderDefinition::getTextureName),
            ResourceLocation.CODEC.optionalFieldOf("model", BladeModelManager.resourceDefaultModel)
                    .forGetter(RenderDefinition::getModelName),
            Codec.INT.optionalFieldOf("summon_sword_color", 0xFF3333FF)
                    .forGetter(RenderDefinition::getSummonedSwordColor),
            Codec.BOOL.optionalFieldOf("color_inverse", false)
                    .forGetter(RenderDefinition::isSummonedSwordColorInverse),
            CarryType.CODEC.optionalFieldOf("carry_type", CarryType.DEFAULT)
                    .forGetter(RenderDefinition::getStandbyRenderType))
            .apply(instance, RenderDefinition::new));

    private final ResourceLocation TextureName;
    private final ResourceLocation ModelName;
    private final int SummonedSwordColor;
    private final boolean SummonedSwordColorInverse;
    private final CarryType StandbyRenderType;

    private RenderDefinition(ResourceLocation texture, ResourceLocation model, int color, boolean colorInverse,
            CarryType standby) {
        this.TextureName = texture;
        this.ModelName = model;
        this.SummonedSwordColor = color;
        this.SummonedSwordColorInverse = colorInverse;
        this.StandbyRenderType = standby;
    }

    public ResourceLocation getModelName() {
        return ModelName;
    }

    public ResourceLocation getTextureName() {
        return TextureName;
    }

    public boolean isSummonedSwordColorInverse() {
        return SummonedSwordColorInverse;
    }

    public int getSummonedSwordColor() {
        return SummonedSwordColor;
    }

    public CarryType getStandbyRenderType() {
        return StandbyRenderType;
    }

    public static class Builder {
        
    }
}