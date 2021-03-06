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

package zone.rong.dexterity;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.collection.DefaultedList;
import zone.rong.dexterity.api.DexterityEntityAttributes;
import zone.rong.dexterity.rpg.skill.perk.Perk;
import zone.rong.dexterity.rpg.skill.perk.PerkBuilder;
import zone.rong.dexterity.rpg.skill.perk.api.InteractionTrigger;
import zone.rong.dexterity.rpg.skill.types.AbstractToolSkill;
import zone.rong.dexterity.rpg.skill.common.api.SkillHandler;

import java.util.ArrayList;
import java.util.List;

public class DexterityHelper {

    public static final String DEXTERITY_GLINT_TAG = "DexterityGlint";
    public static final String DEXTERITY_PLAYER_SKILLS = "DexteritySkills";
    public static final String DEXTERITY_PLAYER_SKILLS_CURRENT_XP = "CurrentXp";
    public static final String DEXTERITY_PLAYER_SKILLS_TOTAL_XP = "TotalXp";
    public static final String DEXTERITY_PLAYER_SKILLS_TOTAL_LEVELS = "Levels";

    public static Perk getGenericBlockBreakPerk(String namespace, String path, AbstractToolSkill<?, ?> skill) {
        return PerkBuilder.of()
                .readyCondition((playerEntity, stack) -> skill.isToolCompatible(stack))
                .perkTrigger(InteractionTrigger.ATTACK_BLOCK)
                .start((player, world, stack) -> {
                    float speed = ((SkillHandler) player).getSkillManager().getSkillEntry(skill).getLevel() >= 50 ? 0.8F : 0.6F;
                    player.getAttributeInstance(DexterityEntityAttributes.MINING_SPEED_PERK).addTemporaryModifier(new EntityAttributeModifier(DexterityEntityAttributes.MINING_SPEED_PERK_UUID, "Bonus Dexterity Mining Speed", speed, EntityAttributeModifier.Operation.ADDITION));
                    return ActionResult.FAIL;
                })
                .end((player, world, stack) -> player.getAttributeInstance(DexterityEntityAttributes.MINING_SPEED_PERK).removeModifier(DexterityEntityAttributes.MINING_SPEED_PERK_UUID))
                .build(namespace, path, skill, level -> 200, level -> 20 + (level * 4));
    }

    // TODO: setup ItemStackWrapper to cast list -> set to find 'unique' stacks
    public static int getUniqueStackCount(DefaultedList<ItemStack> list) {
        List<ItemStack> uniqueStacks = new ArrayList<>();
        for (ItemStack stack : list) {
            if (!stack.isEmpty() && uniqueStacks.stream().noneMatch(u -> ItemStack.areEqual(u, stack))) {
                uniqueStacks.add(stack);
            }
        }
        return uniqueStacks.size();
    }

}
