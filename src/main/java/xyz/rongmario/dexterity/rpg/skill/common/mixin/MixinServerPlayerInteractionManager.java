package xyz.rongmario.dexterity.rpg.skill.common.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.rongmario.dexterity.rpg.skill.common.api.SkillHandler;
import xyz.rongmario.dexterity.rpg.skill.common.api.ServerWorldArtificialBlockStatesHandler;

import static net.minecraft.block.Material.*;

/**
 * Replace the checks with a Set#contains <- this should be modifiable by config of some sort.
 * - JSON -> a singular object that users can add RegistryKeys to (or Identifiers).
 *
 * We can also do these arbitrary checks to give a default amount of xp, should be on par with so and so blocks.
 */
@Mixin(value = ServerPlayerInteractionManager.class, priority = 9999)
public abstract class MixinServerPlayerInteractionManager {

    @Redirect(method = "tryBreakBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;afterBreak(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/item/ItemStack;)V"))
    private void handleAfterBreaking(Block block, World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack stack) {
        if (!((ServerWorldArtificialBlockStatesHandler) world).isArtificial(pos)) {
            if (state.getMaterial() == STONE || state.getMaterial() == AGGREGATE || state.getMaterial() == METAL || state.getMaterial() == SOIL || state.getMaterial() == SOLID_ORGANIC || state.getMaterial() == ORGANIC_PRODUCT || state.getMaterial() == SNOW_LAYER || state.getMaterial() == SNOW_BLOCK || state.getMaterial() == DENSE_ICE || state.getMaterial() == REPAIR_STATION) {
                ((SkillHandler) player).getSkillManager().addBreakXp(stack, state);
            }
        }
        block.afterBreak(world, player, pos, state, blockEntity, stack);
    }

}
