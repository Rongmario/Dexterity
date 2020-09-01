package zone.rong.dexterity;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zone.rong.dexterity.api.DexterityAPI;
import zone.rong.dexterity.api.skill.SkillType;
import zone.rong.dexterity.api.capability.DexterityCapabilities;
import zone.rong.dexterity.networking.DexterityChannel;

import static zone.rong.dexterity.Dexterity.MOD_ID;

@Mod(MOD_ID)
public class Dexterity {

    public static final String MOD_ID = "dexterity";
    public static final String NAME = "Dexterity";
    public static final String VERSION = "Forge-1.0";

    public static final Logger LOGGER = LogManager.getLogger();

    public static Dexterity INSTANCE;

    private final IEventBus modBus;
    private final IEventBus forgeBus;

    {
        INSTANCE = this;

        modBus = FMLJavaModLoadingContext.get().getModEventBus();
        forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.register(this);
    }

    public Dexterity() {
        LOGGER.info("{} {} is now loading...", NAME, VERSION);
        modBus.addListener(this::onNewRegistry);
        modBus.addGenericListener(SkillType.class, this::onSkillRegistry);
        modBus.addListener(this::onCommonSetup);

        forgeBus.addListener(this::itemUseTest);
    }

    private void onNewRegistry(RegistryEvent.NewRegistry event) {
        DexterityAPI.Registries.initRegistries();
    }

    private void onSkillRegistry(RegistryEvent.Register<SkillType> event) {
        DexterityAPI.Skills.init(event.getRegistry());
    }

    private void itemUseTest(PlayerInteractEvent.RightClickItem event) {
        if (event.getPlayer().isSneaking()) {
            DexterityCapabilities.get(event.getItemStack(), DexterityCapabilities.GLINT_MARKER).ifPresent(g -> {
                g.setGlint(!g.hasGlint());
                if (g.hasGlint()) {
                    DexterityCapabilities.get(event.getPlayer(), DexterityCapabilities.SKILL_HOLDER).ifPresent(s -> {
                        s.addXP(10, DexterityAPI.Skills.ALCHEMY);
                    });
                }
            });

        }
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        DexterityChannel.init();
        DexterityCapabilities.register();
    }

    public IEventBus getModBus() {
        return modBus;
    }

    public IEventBus getForgeBus() {
        return forgeBus;
    }

}
