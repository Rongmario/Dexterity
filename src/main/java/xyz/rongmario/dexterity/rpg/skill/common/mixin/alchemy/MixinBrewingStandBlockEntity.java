package xyz.rongmario.dexterity.rpg.skill.common.mixin.alchemy;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.rongmario.dexterity.DexterityData;
import xyz.rongmario.dexterity.api.DexteritySkills;
import xyz.rongmario.dexterity.rpg.skill.common.api.XPStore;
import xyz.rongmario.dexterity.rpg.skill.common.api.alchemy.BrewingStandBlockEntityHandler;

import java.util.UUID;

@Mixin(BrewingStandBlockEntity.class)
public abstract class MixinBrewingStandBlockEntity extends LockableContainerBlockEntity implements XPStore, BrewingStandBlockEntityHandler {

    @Unique private UUID playerUuid;
    @Unique private Object2ObjectMap<UUID, Int2IntMap> xpStore;

    @Shadow private int brewTime;

    protected MixinBrewingStandBlockEntity() {
        super(null);
    }

    @ModifyConstant(method = "tick", constant = @Constant(intValue = 20))
    private int modifyFuel(int fuel) {
        return playerUuid == null ? fuel : fuel + 5; // Temp
    }

    @ModifyConstant(method = "tick", constant = @Constant(intValue = 1, ordinal = 2))
    private int modifyBrewTime(int decrement) {
        return playerUuid == null && this.brewTime < 10 ? decrement : decrement + 9; // Temp
    }

    @Redirect(method = "craft", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/collection/DefaultedList;set(ILjava/lang/Object;)Ljava/lang/Object;"))
    private Object addSkill(DefaultedList<Object> inventory, int index, Object stack) {
        addXp(DexteritySkills.ALCHEMY, 5);
        return inventory.set(index, stack);
    }

    @Inject(method = "fromTag", at = @At("TAIL"))
    private void getUuidFromTag(BlockState state, CompoundTag tag, CallbackInfo ci) {
        fromTag(tag, DexterityData.BREWING_STAND_XP_STORE_KEY);
    }

    @Inject(method = "toTag", at = @At("TAIL"))
    private void addUuidToTag(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir) {
        toTag(tag, DexterityData.BREWING_STAND_XP_STORE_KEY);
    }

    @Override
    public void registerLastPlayer(UUID playerUuid) {
        this.playerUuid = playerUuid;
    }

    @Override
    public void registerLastPlayer(PlayerEntity player) {
        this.playerUuid = player.getUuid();
    }

    @Override
    public void setLocalXpStore(Object2ObjectMap<UUID, Int2IntMap> map) {
        this.xpStore = map;
    }

    @Override
    public UUID getLastUUID() {
        return playerUuid;
    }

    @Override
    public Object2ObjectMap<UUID, Int2IntMap> getLocalXpStore() {
        return xpStore;
    }

}
