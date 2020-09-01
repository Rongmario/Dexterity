package zone.rong.dexterity.api.capability.world;

import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.util.math.BlockPos;

import java.util.Collection;

public interface IArtificialBlocksStore {

    void addArtificialEntry(BlockPos pos);

    void addArtificialEntry(long pos);

    boolean isBlockArtificial(BlockPos pos);

    boolean isBlockArtificial(long pos);

    LongSet getArtificialPosSet();

    Collection<BlockPos> getArtificialBlockPosSet();

}
