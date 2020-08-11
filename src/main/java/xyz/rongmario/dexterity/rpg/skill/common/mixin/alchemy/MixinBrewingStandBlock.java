package xyz.rongmario.dexterity.rpg.skill.common.mixin.alchemy;

import net.minecraft.block.BlockState;
import net.minecraft.block.BrewingStandBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import xyz.rongmario.dexterity.rpg.skill.common.api.alchemy.BrewingStandBlockEntityHandler;

@Mixin(BrewingStandBlock.class)
public class MixinBrewingStandBlock {

    @Inject(method = "onUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;incrementStat(Lnet/minecraft/util/Identifier;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void onOpeningScreen(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir, BlockEntity blockEntity) {
        ((BrewingStandBlockEntityHandler) blockEntity).registerLastPlayer(player);
    }

    @Inject(method = "onPlaced", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/BrewingStandBlockEntity;setCustomName(Lnet/minecraft/text/Text;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void onPlacing(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack, CallbackInfo ci, BlockEntity blockEntity) {
        ((BrewingStandBlockEntityHandler) blockEntity).registerLastPlayer((PlayerEntity) placer);
    }

}
