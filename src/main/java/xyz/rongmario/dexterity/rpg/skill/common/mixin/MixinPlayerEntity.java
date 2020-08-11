package xyz.rongmario.dexterity.rpg.skill.common.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.rongmario.dexterity.api.DexterityEntityTrackers;

import static xyz.rongmario.dexterity.api.DexterityEntityAttributes.MINING_SPEED_PERK;

/**
 * This mixin handles the server feedbacks that the client needs to know about, such as modifying block break speeds
 */
@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity {

    static {
        DexterityEntityTrackers.Player.init();
    }

    MixinPlayerEntity() {
        super(null, null);
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void addDataTrackers(CallbackInfo ci) {
        this.dataTracker.startTracking(DexterityEntityTrackers.Player.REAL_BLOCK_REACH, 4.5F);
        this.dataTracker.startTracking(DexterityEntityTrackers.Player.REAL_ENTITY_REACH, 4.5F);
        this.dataTracker.startTracking(DexterityEntityTrackers.Player.MANA, 0.0F); // TODO - determine if this value should exist on both client/server
    }

    @Inject(method = "getBlockBreakingSpeed", at = @At("TAIL"), cancellable = true)
    private void modifyBlockBreakingSpeed(BlockState block, CallbackInfoReturnable<Float> cir) {
        float original = cir.getReturnValueF();
        float speedModifier = (float) this.getAttributeValue(MINING_SPEED_PERK);
        cir.setReturnValue(speedModifier == 0 ? original : original + (original / speedModifier));
    }

}
