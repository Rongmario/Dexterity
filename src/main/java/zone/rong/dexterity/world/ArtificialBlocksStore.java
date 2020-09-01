package zone.rong.dexterity.world;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zone.rong.dexterity.api.capability.world.IArtificialBlocksStore;

import java.util.Collection;
import java.util.stream.Collectors;

public class ArtificialBlocksStore implements IArtificialBlocksStore {

    private final LongSet artificialBlockPositions;

    public ArtificialBlocksStore(World world) {
        this.artificialBlockPositions = new LongOpenHashSet();
    }

    @Override
    public void addArtificialEntry(BlockPos pos) {
        this.artificialBlockPositions.add(pos.toLong());
    }

    @Override
    public void addArtificialEntry(long pos) {
        this.artificialBlockPositions.add(pos);
    }

    @Override
    public boolean isBlockArtificial(BlockPos pos) {
        return this.artificialBlockPositions.contains(pos.toLong());
    }

    @Override
    public boolean isBlockArtificial(long pos) {
        return this.artificialBlockPositions.contains(pos);
    }

    @Override
    public LongSet getArtificialPosSet() {
        return new LongOpenHashSet(this.artificialBlockPositions);
    }

    @Override
    public Collection<BlockPos> getArtificialBlockPosSet() {
        return this.artificialBlockPositions.stream().map(BlockPos::fromLong).collect(Collectors.toSet());
    }

}
