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

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class NutritionEntry {

    private final LivingEntity entity;
    private final Nutrient nutrient;

    private int percentage;
    private double diminishing;

    public NutritionEntry(LivingEntity entity, Nutrient nutrient) {
        this.entity = entity;
        this.nutrient = nutrient;
    }

    /**
     * @return 1 = right nutrient + added, 0 = right nutrient = diminishing, -1 = wrong nutrient
     */
    public int increase(ItemStack stack) {
        if (nutrient.test(stack)) {
            if (this.diminishing < 1.0F) {
                return 0;
            }
            double hunger = stack.getItem().getFoodComponent().getHunger();
            this.percentage += hunger / 2;
            this.diminishing += hunger / 100;
            return 1;
        }
        return -1;
    }

    public void decrease() {
        this.percentage = Math.min(0, --this.percentage);
    }

    public void setDiminishing(double diminishing) {
        this.diminishing = diminishing;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public double getDiminishing() {
        return diminishing;
    }

    public int getPercentage() {
        return percentage;
    }

}
