package xyz.rongmario.dexterity.rpg.skill.common.api.cultivation;

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
