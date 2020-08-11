package zone.rong.dexterity.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zone.rong.dexterity.api.event.BlockBreakEvent;

/**
 * This mixin fires BlockBreakEvent on the client-side
 */
@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {

    @Shadow @Final private MinecraftClient client;

    @Inject(method = "breakBlock", at = @At("TAIL"))
    private void fireBlockBreakEvent(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        BlockBreakEvent.EVENT.invoker().interact(client.player, client.world, Hand.MAIN_HAND, pos);
    }

}
