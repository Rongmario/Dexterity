/*
 *   Copyright (c) 2020 Rongmario
 *   
 *   Permission is hereby granted, free of charge, to any person obtaining
 *   a copy of this software and associated documentation files (the
 *   "Software"), to deal in the Software without restriction, including
 *   without limitation the rights to use, copy, modify, merge, publish,
 *   distribute, sublicense, and/or sell copies of the Software, and to
 *   permit persons to whom the Software is furnished to do so, subject to
 *   the following conditions:
 *   
 *   The above copyright notice and this permission notice shall be
 *   included in all copies or substantial portions of the Software.
 *   
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *   EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *   MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *   NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 *   LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 *   OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 *   WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
