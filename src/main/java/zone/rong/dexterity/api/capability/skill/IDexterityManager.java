package zone.rong.dexterity.api.capability.skill;

import zone.rong.dexterity.api.skill.SkillType;
import zone.rong.dexterity.skill.SkillContainer;

import java.util.Collection;

public interface IDexterityManager {

    Collection<SkillContainer> getContainers();

    /**
     * @return -1 for unsuccessful (not compatible - hence do not try).
     * 0 for unsuccessful, but do retry with different parameters. More than 0 would be the amount of xp added.
     */
    <XP, C> int addXP(Class<XP> xpClass, XP xpObject, Class<C> compatibleClass, C compatibleObject);

    /**
     * @return 0 if the operation is unsuccessful, more than 0 would be the amount of xp added
     */
    <XP> int addXP(Class<XP> xpClass, XP xpObject);

    /**
     * @return xp added. This takes into account Player's luck level.
     */
    int addXP(SkillType skillType, int xp);

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
