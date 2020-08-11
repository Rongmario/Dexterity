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

import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * This is where the checks happen.
 *
 * This was a pain in the ass to write, debug, check and test.
 *
 * TODO: SpreadableBlock checks, RedstoneOre checks, Leaves checks, and AirBlock elimination
 *
 * Shall I ARR this class?
 *
 *  I love short-circuiting
 *  Stack Walking is still inefficient, without opening up a stacktrace, it's still kills overhead...
 *  We don't allow any air blocks, anything that has random ticks (e.g. - leaves), anything that is scheduled for ticking, and anything setBlockStates that was called from WorldChunk.class
 */
@Mixin(World.class)
public class MixinWorld {

    @Shadow @Final public boolean isClient;

    /*
    @Inject(method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/WorldChunk;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Z)Lnet/minecraft/block/BlockState;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void injectCheck(BlockPos pos, BlockState state, int flags, int maxUpdateDepth, CallbackInfoReturnable<Boolean> cir, WorldChunk chunk) {
        if (
                !this.isClient &&
                !((WorldChunkPostProcessingStatusGetter) chunk).isPostProcessing() &&
                !state.isAir() &&
                !(state.getBlock() instanceof Fertilizable) &&
                !((ServerWorld) (Object) this).getBlockTickScheduler().isScheduled(pos, state.getBlock())
        ) {
            ((ServerWorldArtificialBlockStatesHandler) this).setArtificial(pos);
        }
    }
     */

}
