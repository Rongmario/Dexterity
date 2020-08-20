/*
 *   Copyright (c) 2020 Rongmario
 *
 *   Permission is hereby granted, free of charge, to any person obtaining
 *   a copy of this software and associated documentation files (the
 *   "Software"), to deal in the Software without restriction, including
 *   without limitation the rights to use, copy, modify, merge, publish,
 *   distribute, sublicense, and/or sell copies of the Software, and to
 *   permit persons to whom the Software is furnished to do so, subject to
 *   the following conditions:
 *
 *   The above copyright notice and this permission notice shall be
 *   included in all copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *   EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *   MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *   NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 *   LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 *   OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 *   WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package zone.rong.dexterity.rpg.skill.common.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zone.rong.dexterity.api.DexterityHelper;
import zone.rong.dexterity.api.DexteritySkills;
import zone.rong.dexterity.rpg.skill.client.api.ClientSkillHandler;
import zone.rong.dexterity.rpg.skill.common.api.SkillHandler;

@Mixin(ItemStack.class)
public class MixinItemStack {

    @Shadow private CompoundTag tag;

    @Shadow @Final @Deprecated private Item item;
    @Unique private int stackCountModifier = 1;

    @Inject(method = "hasGlint", at = @At("RETURN"), cancellable = true)
    private void extraHasGlintCheck(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(cir.getReturnValueZ() || (tag != null && tag.contains(DexterityHelper.DEXTERITY_GLINT_TAG)));
    }

    @Inject(method = "getMaxCount", at = @At("RETURN"), cancellable = true)
    private void modifyMaxCount(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(cir.getReturnValueI() * stackCountModifier);
    }

    @Inject(method = "isStackable", at = @At("RETURN"), cancellable = true)
    private void modifyIsStackable(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(cir.getReturnValueZ() || stackCountModifier > 0);
    }

    @Inject(method = "setHolder", at = @At("TAIL"))
    private void whenHolderIsPlayer(Entity holder, CallbackInfo ci) {
        if (holder instanceof PlayerEntity && this.item instanceof PotionItem) {
            if (holder.world.isClient && ((ClientSkillHandler) holder).queryAndGetLevel(DexteritySkills.ALCHEMY) > 1) {
                this.stackCountModifier = 64;
            } else if (!holder.world.isClient && ((SkillHandler) holder).getSkillManager().getLevel(DexteritySkills.ALCHEMY) > 1) {
                this.stackCountModifier = 64;
            }
        }
    }

}
