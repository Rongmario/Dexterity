package zone.rong.dexterity.api;

import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

public class MixinLauncher implements IMixinConnector {

    @Override
    public void connect() {
        Mixins.addConfiguration("dexterity.mixins.json");
    }

}