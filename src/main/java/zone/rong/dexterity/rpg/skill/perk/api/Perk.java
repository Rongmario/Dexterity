package zone.rong.dexterity.rpg.skill.perk.api;

import net.minecraft.text.Text;
import zone.rong.dexterity.rpg.skill.types.Skill;

public interface Perk {

    String getName();

    Text getDisplayName();

    Skill<?> getParentSkill();

}
