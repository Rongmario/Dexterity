package zone.rong.dexterity.api;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class DexterityEntityAttributes {

    public static final EntityAttribute MINING_SPEED_PERK = Registry.register(Registry.ATTRIBUTE, new Identifier("dexterity", "mining_speed_perk"), new ClampedEntityAttribute("attribute.name.mining_speed_perk", 2F, 0F, 200F)).setTracked(true);

    public static void init() {
        FabricDefaultAttributeRegistry.register(EntityType.PLAYER, PlayerEntity.createPlayerAttributes().add(MINING_SPEED_PERK, 0.0F));
    }

}
