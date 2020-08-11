package zone.rong.dexterity.rpg.skill.common.api.cultivation;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import org.apache.commons.lang3.Range;

public enum Diet {

    NONE(0, 10, 5, StatusEffects.SLOWNESS, 2),
    POOR(11, 35, 3, StatusEffects.WEAKNESS, 2),
    NORMAL(36, 60, 1, null, 0),
    GREAT(61, 80, 2, StatusEffects.RESISTANCE, 1),
    COMPLETE(81, 100, 3, StatusEffects.REGENERATION, 1);

    public static final Diet[] VALUES;

    static {
        VALUES = values();
    }

    private final Range<Integer> range;
    private final int amountToSatisfy, effectLevel;
    private final StatusEffect effect;

    Diet(int min, int max, int amountToSatisfy, StatusEffect effect, int effectLevel) {
        this.range = Range.between(min, max);
        this.amountToSatisfy = amountToSatisfy;
        this.effect = effect;
        this.effectLevel = effectLevel;
    }

    public int getAmountToSatisfy() {
        return amountToSatisfy;
    }

    public StatusEffect getEffect() {
        return effect;
    }

    public int getEffectLevel() {
        return effectLevel;
    }

    public static Diet getState(int value) {
        for (Diet diet : VALUES) {
            if (diet.range.contains(value)) {
                return diet;
            }
        }
        throw new IllegalArgumentException("NOT A VALID RANGE");
    }

}
