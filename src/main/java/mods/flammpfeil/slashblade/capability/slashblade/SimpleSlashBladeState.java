package mods.flammpfeil.slashblade.capability.slashblade;

import java.util.Optional;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class SimpleSlashBladeState extends SlashBladeState {

    private final ResourceLocation model;
    private final ResourceLocation texture;
    private final float attack;
    private final int damage;
    

    public SimpleSlashBladeState(ItemStack blade, ResourceLocation model, ResourceLocation texture, float attack, int damage) {
        super(blade);
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
    @Override
    public void setModel(ResourceLocation model) {
    }

    @Override
    public float getBaseAttackModifier() {
        return this.attack;
    }

    @CanIgnoreReturnValue
    @Deprecated
    @Override
    public void setBaseAttackModifier(float baseAttackModifier) {
    }

    @Override
    public ResourceLocation getSlashArtsKey() {
        return super.getSlashArtsKey();
    }

    @CanIgnoreReturnValue
    @Deprecated
    @Override
    public void setSlashArtsKey(ResourceLocation key) {
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
    @Override
    public void setTranslationKey(String translationKey) {
    }

    @Override
    public Optional<ResourceLocation> getTexture() {
        return Optional.ofNullable(texture);
    }

    @CanIgnoreReturnValue
    @Deprecated
    @Override
    public void setTexture(ResourceLocation texture) {
    }

    @Override
    public int getMaxDamage() {
        return this.damage;
    }

    @CanIgnoreReturnValue
    @Deprecated
    @Override
    public void setMaxDamage(int damage) {
    }
}
