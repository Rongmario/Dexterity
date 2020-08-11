package xyz.rongmario.dexterity.rpg.skill.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Formatting;
import xyz.rongmario.dexterity.rpg.skill.types.Skill;

@Environment(EnvType.CLIENT)
public class SkillToast implements Toast {

    private final Skill<?> skill;
    private final int levelPriorToCheck, totalLevel;
    private final ItemStack displayStack;

    private boolean soundPlayed;

    public SkillToast(Skill<?> skill, int levelPriorToCheck, int totalLevel) {
        this.skill = skill;
        this.levelPriorToCheck = levelPriorToCheck;
        this.totalLevel = totalLevel;
        this.displayStack = skill.getDisplayItem();
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public Visibility draw(MatrixStack matrices, ToastManager manager, long startTime) {
        manager.getGame().getTextureManager().bindTexture(TOASTS_TEX);
        int colour = skill.getColour();
        RenderSystem.color3f((colour >> 16) / (float) 255, ((colour >> 8) & 255) / (float) 255, (colour & 255) / (float) 255);
        manager.drawTexture(matrices, 0, 0, 0, 0, this.method_29049(), this.method_29050());
        manager.getGame().textRenderer.draw(matrices, skill.getDisplayName(), 30.0F, 7.0F, colour);
        manager.getGame().textRenderer.draw(matrices, I18n.translate("toast.dexterity.level_up", this.levelPriorToCheck, "➮", this.totalLevel), 30.0F, 18.0F, Formatting.WHITE.getColorValue());
        RenderSystem.pushMatrix();
        manager.getGame().getItemRenderer().renderInGui(displayStack, 8, 8);
        RenderSystem.popMatrix();
        if (startTime == 0L && !soundPlayed) {
            this.soundPlayed = true;
            if (totalLevel % 10 == 0) {
                manager.getGame().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 1.0F));
            } else {
                manager.getGame().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F));
            }
        }
        return startTime >= 2000L ? Visibility.HIDE : Visibility.SHOW;
    }
}
