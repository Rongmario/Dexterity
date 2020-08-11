package xyz.rongmario.dexterity.rpg.skill.client.mixin.cultivation;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.rongmario.dexterity.api.DexterityEntityTrackers;

@Mixin(LivingEntityRenderer.class)
public class MixinLivingEntityRenderer {

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(DDD)V", ordinal = 1))
    private void scaleAnimalEntities(LivingEntity entity, float f, float g, MatrixStack stack, VertexConsumerProvider vcp, int i, CallbackInfo ci) {
        if (entity instanceof AnimalEntity) {
            float widthScale = entity.getDataTracker().get(DexterityEntityTrackers.General.WIDTH_MULTIPLIER);
            stack.scale(widthScale, entity.getDataTracker().get(DexterityEntityTrackers.General.HEIGHT_MULTIPLIER), widthScale);
        }
    }

}
