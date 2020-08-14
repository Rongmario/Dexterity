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

package zone.rong.dexterity;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.options.KeyBinding;
import zone.rong.dexterity.api.DexterityPackets;
import zone.rong.dexterity.rpg.skill.client.api.HudRender;

@Environment(EnvType.CLIENT)
public class DexterityClient implements ClientModInitializer {

    public static final KeyBinding SKILL_MENU = new KeyBinding("key.dexterity.open_skill_menu", 'X', "key.dexterity.category");
    public static final KeyBinding READY_UP = new KeyBinding("key.dexterity.ready_up", 'V', "key.dexterity.category");

    @Override
    public void onInitializeClient() {
        DexterityPackets.registerS2CPackets();
        KeyBindingHelper.registerKeyBinding(SKILL_MENU);
        KeyBindingHelper.registerKeyBinding(READY_UP);
        HudRenderCallback.EVENT.register((stack, delta) -> HudRender.queue.forEach(h -> h.render(stack, delta)));
    }

}
