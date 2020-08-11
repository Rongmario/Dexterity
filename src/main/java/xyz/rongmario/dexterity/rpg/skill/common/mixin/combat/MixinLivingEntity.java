package xyz.rongmario.dexterity.rpg.skill.common.mixin.combat;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.rongmario.dexterity.rpg.skill.common.api.SkillHandler;

// TODO add combo combat xp
@Mixin(LivingEntity.class)
public class MixinLivingEntity {

    @Inject(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;drop(Lnet/minecraft/entity/damage/DamageSource;)V"))
    private void onDeath(DamageSource source, CallbackInfo ci) {
        if (source.getAttacker() instanceof ServerPlayerEntity) {
            ((SkillHandler) source.getAttacker()).getSkillManager().addDamageXp(((ServerPlayerEntity) source.getAttacker()).getMainHandStack(), (LivingEntity) (Object) this);
        }
    }

}
