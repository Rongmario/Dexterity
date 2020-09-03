package zone.rong.dexterity.api.capability.skill;

import zone.rong.dexterity.api.skill.SkillType;
import zone.rong.dexterity.skill.SkillContainer;

import java.util.Collection;

public interface ISkillsHolder {

    Collection<SkillContainer> getContainers();

    <XP, C> void addXP(Class<XP> xpClass, XP xpObject, Class<C> compatibleClass, C compatibleObject);

    void addXP(SkillType skillType, int xp);

    void setLevel(int level);

    void setLevel(SkillType skillType, int level);

    void setCurrentXP(int xp);

    void setCurrentXP(SkillType skillType, int xp);

    void setTotalXP(long xp);

    void setTotalXP(SkillType skillType, long xp);

    int getLevel();

    int getLevel(SkillType skillType);

    int getCurrentXP();

    int getCurrentXP(SkillType skillType);

    long getTotalXP();

    long getTotalXP(SkillType skillType);

    // long getLevelXPRequirement();

    // int getXPNeededForNextLevel();

}
