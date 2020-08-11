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
