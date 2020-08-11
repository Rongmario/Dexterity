package xyz.rongmario.dexterity.mixin.tag;

import net.minecraft.tag.RegistryTagManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.rongmario.dexterity.DexterityData;
import xyz.rongmario.dexterity.api.DexterityTagDelegationManager;
import xyz.rongmario.dexterity.rpg.skill.types.AbstractToolSkill;

@Mixin(RegistryTagManager.class)
public class MixinRegistryTagManager {

    @Inject(method = "apply", at = @At("TAIL"))
    private void applyDelegateManager(CallbackInfo ci) {
        DexterityData.SKILLS.stream()
                .filter(s -> s instanceof AbstractToolSkill)
                .map(s -> ((AbstractToolSkill<?, ?>) s))
                .map(AbstractToolSkill::getManager)
                .forEach(DexterityTagDelegationManager::callback);
    }

}
