package mods.flammpfeil.slashblade.capability.slashblade;

import java.util.Optional;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.DoNotCall;

import net.minecraft.resources.ResourceLocation;

public class SimpleSlashBladeState extends SlashBladeState {
    
    private final ResourceLocation model;
    private final ResourceLocation texture;
    private final float attack;
    private final int damage;
    
    public SimpleSlashBladeState(ResourceLocation model, ResourceLocation texture, float attack, int damage) {
        this.model = model;
        this.attack = attack;
        this.damage = damage;
        this.texture = texture;
    }
    
    @Override
    public Optional<ResourceLocation> getModel() {
        return Optional.ofNullable(model);
    }
    
    @CanIgnoreReturnValue
    @Deprecated
    @DoNotCall("Always throws UnsupportedOperationException")
    @Override
    public void setModel(ResourceLocation model) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float getBaseAttackModifier() {
        return this.attack;
    }

    @CanIgnoreReturnValue
    @Deprecated
    @DoNotCall("Always throws UnsupportedOperationException")
    @Override
    public void setBaseAttackModifier(float baseAttackModifier) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResourceLocation getSlashArtsKey() {
        return super.getSlashArtsKey();
    }

    @CanIgnoreReturnValue
    @Deprecated
    @DoNotCall("Always throws UnsupportedOperationException")
    @Override
    public void setSlashArtsKey(ResourceLocation key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDefaultBewitched() {
        return false;
    }

    @Override
    public String getTranslationKey() {
        return super.getTranslationKey();
    }

    @CanIgnoreReturnValue
    @Deprecated
    @DoNotCall("Always throws UnsupportedOperationException")
    @Override
    public void setTranslationKey(String translationKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<ResourceLocation> getTexture() {
        return Optional.ofNullable(texture);
    }

    @CanIgnoreReturnValue
    @Deprecated
    @DoNotCall("Always throws UnsupportedOperationException")
    @Override
    public void setTexture(ResourceLocation texture) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getMaxDamage() {
        return this.damage;
    }

    @CanIgnoreReturnValue
    @Deprecated
    @DoNotCall("Always throws UnsupportedOperationException")
    @Override
    public void setMaxDamage(int damage) {
        throw new UnsupportedOperationException();
    }
}
