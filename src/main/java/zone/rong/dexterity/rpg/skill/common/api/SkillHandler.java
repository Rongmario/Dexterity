package zone.rong.dexterity.rpg.skill.common.api;

import zone.rong.dexterity.rpg.skill.SkillManager;
import zone.rong.dexterity.rpg.skill.perk.PerkManager;

public interface SkillHandler {

    SkillManager getSkillManager();

    PerkManager getPerkManager();

}
