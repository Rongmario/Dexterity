package xyz.rongmario.dexterity;

import com.mojang.serialization.Lifecycle;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import xyz.rongmario.dexterity.rpg.skill.types.Skill;
import xyz.rongmario.dexterity.rpg.skill.common.api.cultivation.Nutrient;

import java.util.UUID;

public class DexterityData {

    public static final String BREWING_STAND_XP_STORE_KEY = "DexterityBrewingStandXPStore";
    public static final String PLAYER_UUID_KEY = "DexterityUUID";
    public static final String PLAYER_UUID_XP_KEY = "DexterityUUIDXPStore";

    public static final UUID MINING_SPEED_PERK_UUID = MathHelper.randomUuid();
    public static final UUID DAMAGE_AMOUNT_PERK_UUID = MathHelper.randomUuid();

    public static final Identifier MANA_BAR = new Identifier("dexterity", "textures/gui/manabar_new.png");

    public static final RegistryKey<Registry<Skill<?>>> SKILL_KEY = RegistryKey.ofRegistry(new Identifier("dexterity", "skills"));
    public static final RegistryKey<Registry<Nutrient>> NUTRIENT_KEY = RegistryKey.ofRegistry(new Identifier("dexterity", "nutrients"));
    public static final SimpleRegistry<Skill<?>> SKILLS = new SimpleRegistry<>(SKILL_KEY, Lifecycle.stable());
    public static final SimpleRegistry<Nutrient> NUTRIENTS = new SimpleRegistry<>(NUTRIENT_KEY, Lifecycle.stable());

    public static void init() {

    }

}
