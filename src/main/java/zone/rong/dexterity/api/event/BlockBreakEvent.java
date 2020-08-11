package zone.rong.dexterity.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface BlockBreakEvent {

    Event<BlockBreakEvent> EVENT = EventFactory.createArrayBacked(BlockBreakEvent.class,
            listeners -> (player, world, hand, pos) -> {
                for (BlockBreakEvent event : listeners) {
                    ActionResult result = event.interact(player, world, hand, pos);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }
                return ActionResult.PASS;
            }
    );

    ActionResult interact(PlayerEntity player, World world, Hand hand, BlockPos pos);

}
