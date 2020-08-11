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

package zone.rong.dexterity.rpg.skill;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Util;
import zone.rong.dexterity.DexterityData;
import zone.rong.dexterity.DexterityHelper;
import zone.rong.dexterity.rpg.skill.types.CombatSkill;
import zone.rong.dexterity.rpg.skill.types.DigSkill;
import zone.rong.dexterity.rpg.skill.types.Skill;

import java.util.Collection;

public class SkillManager {

    private final Object2ObjectMap<Skill<?>, SkillEntry> containers;

    public SkillManager(ServerPlayerEntity player) {
        this.containers = Util.make(new Object2ObjectOpenHashMap<>(), map -> DexterityData.SKILLS.forEach(s -> map.put(s, new SkillEntry(player, s))));
    }

    public void serialize(CompoundTag parentTag) {
        CompoundTag skillTag = new CompoundTag();
        this.containers.forEach((skill, container) -> {
            CompoundTag individualTag = new CompoundTag();
            individualTag.putInt(DexterityHelper.DEXTERITY_PLAYER_SKILLS_CURRENT_XP, container.getCurrentXp());
            individualTag.putLong(DexterityHelper.DEXTERITY_PLAYER_SKILLS_TOTAL_XP, container.getTotalXp());
            individualTag.putInt(DexterityHelper.DEXTERITY_PLAYER_SKILLS_TOTAL_LEVELS, container.getLevel());
            skillTag.put(skill.toString(), individualTag);
        });
        parentTag.put(DexterityHelper.DEXTERITY_PLAYER_SKILLS, skillTag);
    }

    public void deserialize(CompoundTag parentTag) {
        if (parentTag.contains(DexterityHelper.DEXTERITY_PLAYER_SKILLS, 10)) {
            CompoundTag skillTag = parentTag.getCompound(DexterityHelper.DEXTERITY_PLAYER_SKILLS);
            this.containers.forEach((skill, container) -> {
                CompoundTag individualTag = skillTag.getCompound(skill.toString());
                container.setCurrentXp(individualTag.getInt(DexterityHelper.DEXTERITY_PLAYER_SKILLS_CURRENT_XP));
                container.setTotalXp(individualTag.getLong(DexterityHelper.DEXTERITY_PLAYER_SKILLS_TOTAL_XP));
                container.setTotalLevel(individualTag.getInt(DexterityHelper.DEXTERITY_PLAYER_SKILLS_TOTAL_LEVELS));
            });
        }
    }

    public void addDamageXp(ItemStack stack, LivingEntity entity) {
        this.containers.object2ObjectEntrySet().parallelStream()
                .filter(e -> e.getKey() instanceof CombatSkill)
                .filter(e -> ((CombatSkill) e.getKey()).isToolCompatible(stack))
                .forEach(e -> e.getValue().addXp(((CombatSkill) e.getKey()).getXp(entity.getType())));
    }

    public void addBreakXp(ItemStack stack, BlockState state) {
        this.containers.object2ObjectEntrySet().parallelStream()
                .filter(e -> e.getKey() instanceof DigSkill)
                .filter(e -> ((DigSkill) e.getKey()).isMaterialApplicable(state))
                .filter(e -> ((DigSkill) e.getKey()).isToolCompatible(stack))
                .forEach(e -> e.getValue().addXp(((DigSkill) e.getKey()).getXp(state.getBlock())));
    }

    public void addXp(Skill<?> skill, int xp) {
        this.containers.get(skill).addXp(xp);
    }

    public SkillEntry getSkillEntry(Skill<?> skill) {
        return this.containers.get(skill);
    }

    public Collection<SkillEntry> getSkillEntries() {
        return this.containers.values();
    }

}
