/*
 *  * Copyright (c) 2020 Rongmario
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining
 *  * a copy of this software and associated documentation files (the
 *  * "Software"), to deal in the Software without restriction, including
 *  * without limitation the rights to use, copy, modify, merge, publish,
 *  * distribute, sublicense, and/or sell copies of the Software, and to
 *  * permit persons to whom the Software is furnished to do so, subject to
 *  * the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be
 *  * included in all copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 *  * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 *  * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 *  * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package zone.rong.dexterity.rpg.skill.common.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zone.rong.dexterity.api.DexterityEntityTrackers;

import static zone.rong.dexterity.api.DexterityEntityAttributes.MINING_SPEED_PERK;

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
