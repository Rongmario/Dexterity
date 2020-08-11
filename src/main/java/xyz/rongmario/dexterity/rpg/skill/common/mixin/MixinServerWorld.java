package xyz.rongmario.dexterity.rpg.skill.common.mixin;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.Spawner;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.rongmario.dexterity.rpg.skill.common.api.ServerWorldArtificialBlockStatesHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Mixin(ServerWorld.class)
public class MixinServerWorld implements ServerWorldArtificialBlockStatesHandler {

    @Unique private LongSet ARTIFICIAL_STATES;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void injectAtCtor(MinecraftServer server, Executor workerExecutor, LevelStorage.Session session, ServerWorldProperties properties, RegistryKey<World> registryKey, RegistryKey<DimensionType> registryKey2, DimensionType dimensionType, WorldGenerationProgressListener generationProgressListener, ChunkGenerator chunkGenerator, boolean bl, long l, List<Spawner> list, boolean bl2, CallbackInfo ci) {
        File dexteritySave = ((ServerWorld) (Object) this).getPersistentStateManager().getFile("dexterity");
        try (ObjectInputStream input = new ObjectInputStream(new GZIPInputStream(new FileInputStream(dexteritySave)))) {
            this.ARTIFICIAL_STATES = (LongSet) input.readObject();
        } catch (IOException | ClassNotFoundException e) {
            this.ARTIFICIAL_STATES = new LongOpenHashSet();
        }
    }

    @Inject(method = "saveLevel", at = @At("RETURN"))
    private void saveMap(CallbackInfo ci) {
        Executors.newSingleThreadExecutor().execute(() -> {
            LongSet set = this.ARTIFICIAL_STATES;
            File dexteritySave = ((ServerWorld) (Object) this).getPersistentStateManager().getFile("dexterity");
            try {
                ObjectOutputStream output = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(dexteritySave)));
                output.writeObject(set);
                output.flush();
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public boolean isArtificial(BlockPos pos) {
        return this.ARTIFICIAL_STATES.contains(pos.asLong());
    }

    @Override
    public boolean removeArtificial(BlockPos pos) {
        return this.ARTIFICIAL_STATES.remove(pos.asLong());
    }

    @Override
    public boolean setArtificial(BlockPos pos) {
        return this.ARTIFICIAL_STATES.add(pos.asLong());
    }

    @Override
    public LongSet getArtificialStates() {
        return ARTIFICIAL_STATES;
    }

}
