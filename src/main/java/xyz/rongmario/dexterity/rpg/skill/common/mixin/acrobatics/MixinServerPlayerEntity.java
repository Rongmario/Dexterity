package xyz.rongmario.dexterity.rpg.skill.common.mixin.acrobatics;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.rongmario.dexterity.api.DexteritySkills;
import xyz.rongmario.dexterity.rpg.skill.common.api.SkillHandler;

@Mixin(ServerPlayerEntity.class)
public class MixinServerPlayerEntity {

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    private void handleFall(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source == DamageSource.FALL) {
            ((SkillHandler) this).getSkillManager().addXp(DexteritySkills.ACROBATICS, (int) amount);
        }
    }

}
