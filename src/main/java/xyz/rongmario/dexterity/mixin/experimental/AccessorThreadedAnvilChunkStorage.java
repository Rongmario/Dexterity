package xyz.rongmario.dexterity.mixin.experimental;

import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ThreadedAnvilChunkStorage.class)
public interface AccessorThreadedAnvilChunkStorage {

    @Accessor LongSet getLoadedChunks();

    @Accessor LongSet getUnloadedChunks();

}
