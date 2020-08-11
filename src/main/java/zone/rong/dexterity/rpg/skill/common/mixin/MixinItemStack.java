package zone.rong.dexterity.rpg.skill.common.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class MixinItemStack {

    @Shadow private CompoundTag tag;

    @Inject(method = "hasGlint", at = @At("RETURN"), cancellable = true)
    private void extraHasGlintCheck(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(cir.getReturnValueZ() || (tag != null && tag.contains("DexterityGlint") && tag.getBoolean("DexterityGlint")));
    }

}
