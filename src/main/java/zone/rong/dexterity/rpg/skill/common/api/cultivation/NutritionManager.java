package zone.rong.dexterity.rpg.skill.common.api.cultivation;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundTag;
import zone.rong.dexterity.DexterityData;
import zone.rong.dexterity.api.DexterityNBT;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class NutritionManager {

    private final Map<Nutrient, NutritionEntry> containers;

    public NutritionManager(LivingEntity entity) {
        this.containers = DexterityData.NUTRIENTS.stream().collect(Collectors.toMap(n -> n, n -> new NutritionEntry(entity, n)));
    }

    public void serialize(CompoundTag parentTag) {
        CompoundTag skillTag = new CompoundTag();
        this.containers.forEach((nutrient, container) -> {
            CompoundTag individualTag = new CompoundTag();
            individualTag.putInt(DexterityNBT.Nutrition.PERCENTAGE, container.getPercentage());
            individualTag.putDouble(DexterityNBT.Nutrition.DIMINISH, container.getDiminishing());
            skillTag.put(nutrient.toString(), individualTag);
        });
        parentTag.put(DexterityNBT.Nutrition.ENTRY, skillTag);
    }

    public void deserialize(CompoundTag parentTag) {
        if (parentTag.contains(DexterityNBT.Nutrition.ENTRY, 10)) {
            CompoundTag skillTag = parentTag.getCompound(DexterityNBT.Nutrition.ENTRY);
            this.containers.forEach((nutrient, container) -> {
                CompoundTag individualTag = skillTag.getCompound(nutrient.toString());
                container.setPercentage(individualTag.getInt(DexterityNBT.Nutrition.PERCENTAGE));
                container.setDiminishing(individualTag.getDouble(DexterityNBT.Nutrition.DIMINISH));
            });
        }
    }

    public NutritionEntry getNutritionEntry(Nutrient nutrient) {
        return this.containers.get(nutrient);
    }

    public Collection<NutritionEntry> getNutritionEntries() {
        return this.containers.values();
    }

    public Diet getDietState() {
        int average = (int) this.containers.values().stream().mapToInt(NutritionEntry::getPercentage).average().getAsDouble();
        return Diet.getState(average);
    }

}
