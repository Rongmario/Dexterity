package zone.rong.dexterity.rpg.skill.common.mixin.archery;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import zone.rong.dexterity.api.DexterityNBT;

@Mixin(BowItem.class)
public abstract class MixinBowItem {

    @Shadow public abstract void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks);

    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setCurrentHand(Lnet/minecraft/util/Hand;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void onConsumeResult(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir, ItemStack stack) {
        if (stack.getTag().contains(DexterityNBT.Skills.RANGED_QUICK_FIRE)) {
            this.onStoppedUsing(stack, world, user, 100);
        }
    }

}
