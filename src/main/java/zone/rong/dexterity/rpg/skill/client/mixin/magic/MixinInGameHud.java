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

package zone.rong.dexterity.rpg.skill.client.mixin.magic;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import zone.rong.dexterity.DexterityData;
import zone.rong.dexterity.api.DexterityEntityTrackers;

@Mixin(InGameHud.class)
public class MixinInGameHud extends DrawableHelper {

    @Shadow @Final private MinecraftClient client;
    @Shadow @Final private ChatHud chatHud;

    @Shadow private int scaledHeight;
    @Shadow private int scaledWidth;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;hasJumpingMount()Z", shift = At.Shift.BEFORE, by = 1), locals = LocalCapture.CAPTURE_FAILHARD)
    private void renderManaBar(MatrixStack stack, float f, CallbackInfo ci, TextRenderer textRenderer, int i) {
        // if (!this.chatHud.isChatFocused()) { // isChatFocused is private now
            this.client.getTextureManager().bindTexture(DexterityData.MANA_BAR);
            int progress = (int) (this.client.player.getDataTracker().get(DexterityEntityTrackers.Player.MANA) * 100F);
            this.drawTexture(stack, i + 41 /* this is 50 */, this.scaledHeight - 45, 0, 0, 100, 5);
            this.drawTexture(stack, i + 41 /* this is 50 */, this.scaledHeight - 45, 0, 5, progress, 5);
        // }
    }

}
