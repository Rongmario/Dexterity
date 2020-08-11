package xyz.rongmario.dexterity.rpg.skill.common.mixin;

import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.rongmario.dexterity.rpg.skill.common.api.ServerWorldArtificialBlockStatesHandler;

@Mixin(BlockItem.class)
public class MixinBlockItem {

    @Redirect(method = "place(Lnet/minecraft/item/ItemPlacementContext;Lnet/minecraft/block/BlockState;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemPlacementContext;getBlockPos()Lnet/minecraft/util/math/BlockPos;"))
    private BlockPos checkForPlayerPlacement(ItemPlacementContext ctx) {
        if (ctx.getPlayer() != null && !ctx.getWorld().isClient) {
            ((ServerWorldArtificialBlockStatesHandler) ctx.getWorld()).setArtificial(ctx.getBlockPos());
        }
        return ctx.getBlockPos();
    }

}
