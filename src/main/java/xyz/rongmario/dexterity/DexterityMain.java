package xyz.rongmario.dexterity;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.rongmario.dexterity.api.DexterityEntityAttributes;
import xyz.rongmario.dexterity.api.DexterityPackets;
import xyz.rongmario.dexterity.api.DexteritySkills;
import xyz.rongmario.dexterity.rpg.skill.perk.BasePerk;
import xyz.rongmario.dexterity.rpg.skill.types.Skill;

import static xyz.rongmario.dexterity.DexterityData.*;

public class DexterityMain implements ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger("Dexterity");

    @Override
    public void onInitialize() {
        DexterityData.init();
        DexteritySkills.init();
        DexterityEntityAttributes.init();
        DexterityPackets.registerC2SPackets();
        SKILLS.stream().map(Skill::getPerks).forEach(perks -> perks.forEach(BasePerk::registerExtra));
    }

}
