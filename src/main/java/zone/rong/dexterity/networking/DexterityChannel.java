package zone.rong.dexterity.networking;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import zone.rong.dexterity.Dexterity;
import zone.rong.dexterity.networking.packets.S2CLevelUp;

public class DexterityChannel {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE;

    private static int id = 0;

    static {
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(Dexterity.MOD_ID, "network"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

        INSTANCE.registerMessage(id++, S2CLevelUp.class, S2CLevelUp::encode, S2CLevelUp::decode, S2CLevelUp::handle);
    }

    public static void init() { }

}
