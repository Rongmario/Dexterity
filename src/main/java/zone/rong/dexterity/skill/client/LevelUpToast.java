package zone.rong.dexterity.skill.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import zone.rong.dexterity.api.DexterityAPI;
import zone.rong.dexterity.api.skill.SkillType;

// TODO: 1.16 - update MatrixStack impl
@OnlyIn(Dist.CLIENT)
public class LevelUpToast implements IToast {

    private final SkillType skillType;
    private final int level;

    private boolean soundPlayed;

    public LevelUpToast(int skillTypeRawId, int level) {
        this.skillType = DexterityAPI.getFromRaw(DexterityAPI.Registries.SKILLS, skillTypeRawId);
        this.level = level;
    }

    @Override
    public Visibility draw(ToastGui gui, long startTime) {
        int colour = skillType.getColour();
        RenderSystem.color3f((colour >> 16) / (float) 255, ((colour >> 8) & 255) / (float) 255, (colour & 255) / (float) 255);
        gui.getMinecraft().textureManager.bindTexture(TEXTURE_TOASTS);
        gui.blit(0, 0, 0, 32, 160, 32);
        gui.getMinecraft().fontRenderer.drawString(I18n.format(skillType.getName().getKey()), 30.0F, 7.0F, colour);
        gui.getMinecraft().fontRenderer.drawString(I18n.format("toast.dexterity.level_up", this.level - 1, "->", this.level)/*I18n.format("toast.dexterity.level_up", this.level - 1, "➮", this.level)*/, 30.0F, 18.0F, TextFormatting.WHITE.getColor());
        RenderSystem.pushMatrix();
        skillType.toastRenderCallback().render(this, gui, 32, 160);
        RenderSystem.popMatrix();
        if (startTime == 0L && !soundPlayed) {
            this.soundPlayed = true;
            if (this.level % 10 == 0) {
                gui.getMinecraft().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 0.7F));
            } else {
                gui.getMinecraft().getSoundHandler().play(SimpleSound.master(SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0F, 0.7F));
            }
        }
        return startTime >= 2000L ? Visibility.HIDE : Visibility.SHOW;
    }

}
