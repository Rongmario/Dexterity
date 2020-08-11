package xyz.rongmario.dexterity.rpg.skill.common.mixin;

import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.rongmario.dexterity.rpg.skill.common.api.WorldChunkPostProcessingStatus;

@Mixin(WorldChunk.class)
public class MixinWorldChunk implements WorldChunkPostProcessingStatus {

    @Unique private boolean isPostProcessing = false;

    @Inject(method = "runPostProcessing", at = @At("HEAD"))
    private void before(CallbackInfo ci) {
        this.isPostProcessing = true;
    }

    @Inject(method = "runPostProcessing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/WorldChunk;disableTickSchedulers()V"))
    private void after(CallbackInfo ci) {
        this.isPostProcessing = false;
    }

    @Override
    public boolean isPostProcessing() {
        return isPostProcessing;
    }

}
