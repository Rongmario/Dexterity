/*
 *  * Copyright (c) 2020 Rongmario
 *  * 
 *  * Permission is hereby granted, free of charge, to any person obtaining
 *  * a copy of this software and associated documentation files (the
 *  * "Software"), to deal in the Software without restriction, including
 *  * without limitation the rights to use, copy, modify, merge, publish,
 *  * distribute, sublicense, and/or sell copies of the Software, and to
 *  * permit persons to whom the Software is furnished to do so, subject to
 *  * the following conditions:
 *  * 
 *  * The above copyright notice and this permission notice shall be
 *  * included in all copies or substantial portions of the Software.
 *  * 
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 *  * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 *  * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 *  * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
