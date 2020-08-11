package zone.rong.dexterity.rpg.skill.common.mixin.acrobatics;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zone.rong.dexterity.api.DexterityEntityTrackers;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {

    @Shadow @Final private MinecraftClient client;

    @Inject(method = "getReachDistance", at = @At("RETURN"), cancellable = true)
    private void modifyReachDistance(CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(this.client.player.getDataTracker().get(DexterityEntityTrackers.Player.REAL_BLOCK_REACH));
    }

}
