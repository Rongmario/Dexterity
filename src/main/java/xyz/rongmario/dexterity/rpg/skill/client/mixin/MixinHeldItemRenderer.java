package xyz.rongmario.dexterity.rpg.skill.client.mixin;

import net.minecraft.client.render.item.HeldItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import xyz.rongmario.dexterity.rpg.skill.client.api.HeldItemHandler;

/**
 * TODO: Raise up the main hand model when the player is ready
 */
@Mixin(HeldItemRenderer.class)
public class MixinHeldItemRenderer implements HeldItemHandler {

    @Unique private boolean dexterity$ready;

    @Override
    public void ready(boolean ready) {
        this.dexterity$ready = ready;
    }

}
