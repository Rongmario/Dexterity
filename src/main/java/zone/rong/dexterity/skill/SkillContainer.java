package zone.rong.dexterity.skill;

import zone.rong.dexterity.api.skill.SkillType;

public class SkillContainer {

    public final SkillType skillType;

    public long totalXP;

    public int level;
    public int currentXP;

    SkillContainer(SkillType skillType) {
        this.skillType = skillType;
    }

}
