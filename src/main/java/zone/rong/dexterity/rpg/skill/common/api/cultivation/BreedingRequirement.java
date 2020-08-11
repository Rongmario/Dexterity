package zone.rong.dexterity.rpg.skill.common.api.cultivation;

import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

/**
 * If the following requirement is met - breeding commences.
 */
public interface BreedingRequirement {

    boolean test(AnimalEntity entity, AnimalEntity otherEntity, BlockPos pos, ServerWorld world, int loveTicks/*breedingTicks*/);

}
