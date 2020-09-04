package zone.rong.dexterity.skill.hooks;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import zone.rong.dexterity.Dexterity;
import zone.rong.dexterity.api.capability.DexterityCapabilities;

public class Events {

    private static final IEventBus forgeBus = Dexterity.INSTANCE.getForgeBus();

    public static void init() {
        forgeBus.addListener(Events::onLivingDeath);
    }

    private static void onLivingDeath(LivingDeathEvent event) {
        Entity source = event.getSource().getImmediateSource();
        if (source instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) source;
            source.getCapability(DexterityCapabilities.SKILL_HOLDER).ifPresent(holder -> holder.addXP(EntityType.class, event.getEntity().getType(), Item.class, player.getHeldItemMainhand().getItem()));
        }
    }

}
