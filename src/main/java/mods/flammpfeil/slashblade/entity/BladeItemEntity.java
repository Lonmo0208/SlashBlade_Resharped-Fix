package mods.flammpfeil.slashblade.entity;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.init.DefaultResources;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.network.NetworkHooks;

public class BladeItemEntity extends ItemEntity {
    private static final EntityDataAccessor<String> DATA_MODEL = SynchedEntityData.defineId(BladeItemEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> DATA_TEXTURE = SynchedEntityData.defineId(BladeItemEntity.class, EntityDataSerializers.STRING);

    public BladeItemEntity(EntityType<? extends BladeItemEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(DATA_MODEL, DefaultResources.resourceDefaultModel.toString());
        this.getEntityData().define(DATA_TEXTURE, DefaultResources.resourceDefaultTexture.toString());
    }

    public ResourceLocation getModel() {
        return ResourceLocation.tryParse(this.getEntityData().get(DATA_MODEL));
    }

    public void setModel(ResourceLocation model) {
        this.getEntityData().set(DATA_MODEL, model.toString());
    }

    public ResourceLocation getTexture() {
        return ResourceLocation.tryParse(this.getEntityData().get(DATA_TEXTURE));
    }

    public void setTexture(ResourceLocation texture) {
        this.getEntityData().set(DATA_TEXTURE, texture.toString());
    }

    public void init() {
        this.setInvulnerable(true);
        CompoundTag compoundnbt = this.saveWithoutId(new CompoundTag());
        compoundnbt.remove("Dimension");
        compoundnbt.putShort("Health", (short) 100);
        compoundnbt.putShort("Age", Short.MIN_VALUE);
        this.load(compoundnbt);
    }

    public static BladeItemEntity createInstanceFromPacket(PlayMessages.SpawnEntity packet, Level worldIn) {
        return new BladeItemEntity(SlashBlade.RegistryEvents.BladeItem, worldIn);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void tick() {
        if (onGround() && tickCount % 40 == 0) {
            tickCount++;
        }
        super.tick();

        if (!this.isInWater() && !onGround() && tickCount % 6 == 0) {
            this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 0.5F, 2.5F);
        }

        if (this.level().isClientSide()) {
            if (random.nextInt(5) == 0 && getAirSupply() < 0) {
                Direction direction = Direction.UP;
                double d0 = this.getX() - random.nextFloat() * 0.1F;
                double d1 = this.getY() - random.nextFloat() * 0.1F;
                double d2 = this.getZ() - random.nextFloat() * 0.1F;
                double d3 = 0.4F - (random.nextFloat() + random.nextFloat()) * 0.4F;
                this.level().addParticle(ParticleTypes.PORTAL, d0 + direction.getStepX() * d3,
                        d1 + 2 + direction.getStepY() * d3, d2 + direction.getStepZ() * d3,
                        random.nextGaussian() * 0.005D, -2, random.nextGaussian() * 0.005D);
            }

            if (!this.onGround() && !this.isInWater() && random.nextInt(3) == 0) {
                Direction direction = Direction.UP;
                double d0 = this.getX() - random.nextFloat() * 0.1F;
                double d1 = this.getY() - random.nextFloat() * 0.1F;
                double d2 = this.getZ() - random.nextFloat() * 0.1F;
                double d3 = 0.4F - (random.nextFloat() + random.nextFloat()) * 0.4F;
                this.level().addParticle(ParticleTypes.END_ROD, d0 + direction.getStepX() * d3,
                        d1 + direction.getStepY() * d3, d2 + direction.getStepZ() * d3,
                        random.nextGaussian() * 0.005D, random.nextGaussian() * 0.005D, random.nextGaussian() * 0.005D);
            }
        }
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource ds) {
        super.causeFallDamage(distance, damageMultiplier, ds);

        int i = Mth.ceil(distance);
        if (i > 0) {
            this.playSound(SoundEvents.GENERIC_BIG_FALL, 1.0F, 1.0F);
            this.hurt(this.level().damageSources().fall(), (float) i);
            BlockPos pos = new BlockPos(Mth.floor(this.getX()), Mth.floor(this.getY() - 0.2F), Mth.floor(this.getZ()));
            BlockState blockstate = this.level().getBlockState(pos);
            if (!blockstate.isAir()) {
                SoundType soundtype = blockstate.getSoundType(level(), pos, this);
                this.playSound(soundtype.getFallSound(), soundtype.getVolume() * 0.5F, soundtype.getPitch() * 0.75F);
            }

            if (this.isCurrentlyGlowing() && getAirSupply() < 0) {
                this.setGlowingTag(false);
            }
        }

        return false;
    }

    @Override
    public float getLightLevelDependentMagicValue() {
        return getAirSupply() < 0 ? 15728880 : super.getLightLevelDependentMagicValue();
    }
}