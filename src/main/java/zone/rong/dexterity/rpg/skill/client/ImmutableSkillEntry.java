package zone.rong.dexterity.rpg.skill.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import zone.rong.dexterity.rpg.skill.SkillEntry;

/**
 * Client-Side and Immutable View of {@link SkillEntry} - primarily for packet stuff
 */
@Environment(EnvType.CLIENT)
public class ImmutableSkillEntry {

    public final long totalXp, currentXp;
    public final int totalLevels;

    public ImmutableSkillEntry(long totalXp, long currentXp, int totalLevels) {
        this.totalXp = totalXp;
        this.currentXp = currentXp;
        this.totalLevels = totalLevels;
    }

}
