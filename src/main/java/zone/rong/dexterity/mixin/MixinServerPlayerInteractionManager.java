package zone.rong.dexterity.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zone.rong.dexterity.api.event.BlockBreakEvent;

/**
 * This mixin fires BlockBreakEvent on the server-side
 */
@Mixin(ServerPlayerInteractionManager.class)
public class MixinServerPlayerInteractionManager {

    @Shadow public ServerPlayerEntity player;
    @Shadow public ServerWorld world;

    @Inject(method = "tryBreakBlock", at = @At("TAIL"))
    private void fireBlockBreakEvent(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        BlockBreakEvent.EVENT.invoker().interact(player, world, Hand.MAIN_HAND, pos);
    }

    // Proper redirect under this to send back an "everything's alright!" packet to client, also quieting this bitch up for the time being
    @Redirect(method = "processBlockBreakingAction", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;warn(Ljava/lang/String;)V", remap = false))
    private void fuckThisShitMessage(Logger logger, String message) { }

}
