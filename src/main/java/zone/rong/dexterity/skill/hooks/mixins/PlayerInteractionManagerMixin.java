package zone.rong.dexterity.skill.hooks.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import zone.rong.dexterity.api.capability.DexterityCapabilities;

@Mixin(PlayerInteractionManager.class)
public class PlayerInteractionManagerMixin {

    @Redirect(method = "tryHarvestBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;harvestBlock(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/tileentity/TileEntity;Lnet/minecraft/item/ItemStack;)V"))
    private void onHarvestingBlock(Block block, World world, PlayerEntity player, BlockPos pos, BlockState state, TileEntity te, ItemStack stack) {
        block.harvestBlock(world, player, pos, state, te, stack);
        player.getCapability(DexterityCapabilities.SKILL_HOLDER).ifPresent(holder -> holder.addXP(Block.class, block, Item.class, stack.getItem()));
    }

}
