package zone.rong.dexterity;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zone.rong.dexterity.api.DexterityEntityAttributes;
import zone.rong.dexterity.api.DexterityPackets;
import zone.rong.dexterity.api.DexteritySkills;
import zone.rong.dexterity.rpg.skill.perk.BasePerk;
import zone.rong.dexterity.rpg.skill.types.Skill;

import static zone.rong.dexterity.DexterityData.*;

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
