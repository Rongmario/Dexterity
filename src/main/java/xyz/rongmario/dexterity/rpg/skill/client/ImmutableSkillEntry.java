package xyz.rongmario.dexterity.rpg.skill.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Client-Side and Immutable View of {@link xyz.rongmario.dexterity.rpg.skill.SkillEntry} - primarily for packet stuff
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
