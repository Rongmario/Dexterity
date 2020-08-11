package xyz.rongmario.dexterity;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.options.KeyBinding;
import xyz.rongmario.dexterity.api.DexterityPackets;

@Environment(EnvType.CLIENT)
public class DexterityClient implements ClientModInitializer {

    // TODO: make the keybind customizable
    public static final KeyBinding SKILL_MENU = new KeyBinding("key.dexterity.open_skill_menu", 'F', "key.dexterity.category");

    @Override
    public void onInitializeClient() {
        DexterityPackets.registerS2CPackets();
        KeyBindingHelper.registerKeyBinding(SKILL_MENU);
    }

}
