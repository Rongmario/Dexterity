package xyz.rongmario.dexterity.rpg.skill.perk.api;

import net.minecraft.text.Text;
import xyz.rongmario.dexterity.rpg.skill.types.Skill;

public interface Perk {

    String getName();

    Text getDisplayName();

    Skill<?> getParentSkill();

}
