package xyz.rongmario.dexterity.rpg.skill.common.mixin.cultivation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.rongmario.dexterity.api.DexterityEntityTrackers;
import xyz.rongmario.dexterity.api.DexterityNBT;
import xyz.rongmario.dexterity.rpg.skill.common.api.cultivation.SizeHandler;
import xyz.rongmario.dexterity.rpg.skill.common.api.cultivation.NutritionHandler;
import xyz.rongmario.dexterity.rpg.skill.common.api.cultivation.NutritionManager;

@Mixin(AnimalEntity.class)
public abstract class MixinAnimalEntity extends PassiveEntity implements NutritionHandler, SizeHandler {

    // All AnimalEntities have a spawn height bound of 0.75x - 1.3x and width bound of 0.8-1.28x

    @Unique private NutritionManager nutritionManager;
    @Unique private float widthMultiplier = 1.0F;
    @Unique private float heightMultiplier = 1.0F;
    @Unique private boolean aggro;

    static {
        DexterityEntityTrackers.General.init();
    }

    protected MixinAnimalEntity() {
        super(null, null);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void constructNutritionManager(EntityType<? extends AnimalEntity> entityType, World world, CallbackInfo ci) {
        this.nutritionManager = new NutritionManager((AnimalEntity) (Object) this);
    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(DexterityEntityTrackers.General.WIDTH_MULTIPLIER, 1.0F);
        this.dataTracker.startTracking(DexterityEntityTrackers.General.HEIGHT_MULTIPLIER, 1.0F);
    }

    @Inject(method = "readCustomDataFromTag", at = @At("TAIL"))
    private void deserializeNutritionManager(CompoundTag tag, CallbackInfo ci) {
        this.nutritionManager.deserialize(tag);
        if (tag.contains(DexterityNBT.EntityStats.ENTRY)) {
            CompoundTag entryTag = tag.getCompound(DexterityNBT.EntityStats.ENTRY);
            this.setDimensions(entryTag.getFloat(DexterityNBT.EntityStats.WIDTH_MULTIPLIER), entryTag.getFloat(DexterityNBT.EntityStats.HEIGHT_MULTIPLIER));
        }
    }

    @Inject(method = "writeCustomDataToTag", at = @At("TAIL"))
    private void serializeNutritionManager(CompoundTag tag, CallbackInfo ci) {
        this.nutritionManager.serialize(tag);
        CompoundTag entryTag = new CompoundTag();
        entryTag.putFloat(DexterityNBT.EntityStats.WIDTH_MULTIPLIER, this.widthMultiplier);
        entryTag.putFloat(DexterityNBT.EntityStats.HEIGHT_MULTIPLIER, this.heightMultiplier);
        tag.put(DexterityNBT.EntityStats.ENTRY, entryTag);
    }

    // TODO - higher level of player's cultivation means less hunger foods can be used
    // TODO - make 'produce' other than wheat available to be eaten. Such as sugar cane and such.
    @Inject(method = "isBreedingItem", at = @At("RETURN"), cancellable = true)
    private void acceptAllFoods(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(stack.isFood() || stack.getItem() == Items.WHEAT);
    }

    @Redirect(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;decrement(I)V"))
    private void modifyAmountEaten(ItemStack stack, int amount) {
        stack.decrement(Math.min(this.nutritionManager.getDietState().getAmountToSatisfy(), stack.getCount()));
    }

    // TODO: possible new particle?
    @Environment(EnvType.CLIENT)
    @Inject(method = "handleStatus", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/PassiveEntity;handleStatus(B)V"))
    private void injectAggroStatus(byte status, CallbackInfo ci) {
        if (status == 127) {
            for (int i = 0; i < 7; ++i) {
                double d = this.random.nextGaussian() * 0.02D;
                double e = this.random.nextGaussian() * 0.02D;
                double f = this.random.nextGaussian() * 0.02D;
                this.world.addParticle(ParticleTypes.ANGRY_VILLAGER, this.getParticleX(1.0D), this.getRandomBodyY() + 0.5D, this.getParticleZ(1.0D), d, e, f);
            }
        }
    }

    @Override
    public NutritionManager getNutritionManager() {
        return nutritionManager;
    }

    @Override
    public void setWidthMultiplier(float widthMultiplier) {
        this.widthMultiplier = widthMultiplier;
    }

    @Override
    public void setHeightMultiplier(float heightMultiplier) {
        this.heightMultiplier = heightMultiplier;
    }

    @Override
    public void setDimensions(float widthMultiplier, float heightMultiplier) {
        this.widthMultiplier = widthMultiplier;
        this.heightMultiplier = heightMultiplier;
        this.dimensions = new EntityDimensions(this.getType().getWidth() * widthMultiplier, this.getType().getHeight() * heightMultiplier, false);
        this.dataTracker.set(DexterityEntityTrackers.General.WIDTH_MULTIPLIER, widthMultiplier);
        this.dataTracker.set(DexterityEntityTrackers.General.HEIGHT_MULTIPLIER, heightMultiplier);
    }
}
