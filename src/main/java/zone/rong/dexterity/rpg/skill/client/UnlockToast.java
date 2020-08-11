/*
 *   Copyright (c) 2020 Rongmario
 *
 *   Permission is hereby granted, free of charge, to any person obtaining
 *   a copy of this software and associated documentation files (the
 *   "Software"), to deal in the Software without restriction, including
 *   without limitation the rights to use, copy, modify, merge, publish,
 *   distribute, sublicense, and/or sell copies of the Software, and to
 *   permit persons to whom the Software is furnished to do so, subject to
 *   the following conditions:
 *
 *   The above copyright notice and this permission notice shall be
 *   included in all copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *   EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *   MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *   NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 *   LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 *   OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 *   WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package zone.rong.dexterity.rpg.skill.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Formatting;
import zone.rong.dexterity.api.Unlockable;

@Environment(EnvType.CLIENT)
public class UnlockToast implements Toast {

    private final Unlockable unlockable;

    private boolean soundPlayed;

    public UnlockToast(Unlockable unlockable) {
        this.unlockable = unlockable;
    }

    @Override
    public Visibility draw(MatrixStack matrices, ToastManager manager, long startTime) {
        manager.getGame().getTextureManager().bindTexture(TOASTS_TEX);
        int colour = unlockable.getColour();
        RenderSystem.color3f((colour >> 16) / (float) 255, ((colour >> 8) & 255) / (float) 255, (colour & 255) / (float) 255);
        manager.drawTexture(matrices, 0, 0, 0, 0, this.method_29049(), this.method_29050());
        manager.getGame().textRenderer.draw(matrices, I18n.translate("toast.dexterity.unlock", unlockable.getName()), 30.0F, 12.0F, Formatting.WHITE.getColorValue());
        RenderSystem.pushMatrix();
        manager.getGame().getItemRenderer().renderInGui(unlockable.getDisplayItem(), 8, 8);
        RenderSystem.popMatrix();
        if (startTime == 0L && !soundPlayed) {
            this.soundPlayed = true;
            manager.getGame().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_TRIPWIRE_CLICK_OFF, 1.0F, 1.0F));
            manager.getGame().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_TRIPWIRE_CLICK_ON, 1.0F, 1.0F));
        }
        return startTime >= 2000L ? Visibility.HIDE : Visibility.SHOW;
    }

}
