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

package zone.rong.dexterity.mixin.experimental;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ProtoChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer {

    @Shadow public abstract ServerWorld getWorld(RegistryKey<World> registryKey);

    @Shadow @Final private StructureManager structureManager;

    @Inject(method = "prepareStartRegion", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;updateMobSpawnOptions()V"))
    private void wow(WorldGenerationProgressListener worldGenerationProgressListener, CallbackInfo ci) {
        ServerWorld world = this.getWorld(World.OVERWORLD);
        ThreadedAnvilChunkStorage storage = world.getChunkManager().threadedAnvilChunkStorage;
        AccessorThreadedAnvilChunkStorage tacs = (AccessorThreadedAnvilChunkStorage) storage;
        tacs.getLoadedChunks().stream().map(ChunkPos::new).forEach(System.out::println);
        tacs.getUnloadedChunks().stream().map(ChunkPos::new).forEach(System.out::println);
        ProtoChunk chunk = ChunkSerializer.deserialize(world, this.structureManager, world.getPointOfInterestStorage(), new ChunkPos(new BlockPos(277, 64, 145)), new CompoundTag());
        System.out.println("ChunkPos: " + chunk.getPos());
        System.out.println("BlockState at " + new BlockPos(277, 64, 145).toString() + " is: " + chunk.getBlockState(new BlockPos(277, 64, 145)).toString());
    }

}
