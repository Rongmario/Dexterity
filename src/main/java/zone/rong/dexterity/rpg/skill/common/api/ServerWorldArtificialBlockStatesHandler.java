package zone.rong.dexterity.rpg.skill.common.api;

import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.util.math.BlockPos;

// p0nki - SWABSH
public interface ServerWorldArtificialBlockStatesHandler {

    boolean isArtificial(BlockPos pos);

    boolean setArtificial(BlockPos pos);

    boolean removeArtificial(BlockPos pos);

    LongSet getArtificialStates();

}
