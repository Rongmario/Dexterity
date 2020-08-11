package xyz.rongmario.dexterity.rpg.skill.common.api;

import xyz.rongmario.dexterity.rpg.skill.SkillManager;
import xyz.rongmario.dexterity.rpg.skill.perk.PerkManager;

public interface SkillHandler {

    SkillManager getSkillManager();

    PerkManager getPerkManager();

}
