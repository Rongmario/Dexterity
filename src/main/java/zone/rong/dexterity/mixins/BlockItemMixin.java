package zone.rong.dexterity.mixins;

import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.ActionResultType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zone.rong.dexterity.api.capability.DexterityCapabilities;

@Mixin(BlockItem.class)
public class BlockItemMixin {

    @Inject(method = "tryPlace", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/criterion/PlacedBlockTrigger;trigger(Lnet/minecraft/entity/player/ServerPlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/item/ItemStack;)V", shift = At.Shift.BEFORE))
    private void checkForPlayerPlacement(BlockItemUseContext ctx, CallbackInfoReturnable<ActionResultType> cir) {
        if (ctx.getPlayer() != null && !ctx.getWorld().isRemote) {
            ctx.getWorld().getCapability(DexterityCapabilities.ARTIFICIAL_BLOCKS_STORE).ifPresent(store -> store.addArtificialEntry(ctx.getPos()));
        }
    }

}
