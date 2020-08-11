package xyz.rongmario.dexterity.rpg.skill.common.mixin.acrobatics;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import xyz.rongmario.dexterity.api.DexterityEntityTrackers;

@Mixin(ServerPlayerInteractionManager.class)
public class MixinServerPlayerInteractionManager {

    @Shadow public ServerPlayerEntity player;

    @ModifyConstant(method = "processBlockBreakingAction", constant = @Constant(doubleValue = 36.0D))
    private double modifyConstantToCheckBlockDistance(double original) {
        // double modified = this.player.getDataTracker().get(DexterityEntityTrackers.Player.REAL_REACH);
        // return modified * modified;
        return this.player.getDataTracker().get(DexterityEntityTrackers.Player.REAL_BLOCK_REACH) * 9;
    }

}
