package zone.rong.dexterity;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.collection.DefaultedList;
import zone.rong.dexterity.api.DexterityEntityAttributes;
import zone.rong.dexterity.rpg.skill.perk.GenericPerk;
import zone.rong.dexterity.rpg.skill.perk.PerkBuilder;
import zone.rong.dexterity.rpg.skill.perk.api.InteractionTrigger;
import zone.rong.dexterity.rpg.skill.types.AbstractToolSkill;
import zone.rong.dexterity.rpg.skill.common.api.SkillHandler;

import java.util.ArrayList;
import java.util.List;

public class DexterityHelper {

    public static final String DEXTERITY_GLINT_TAG = "DexterityGlint";
    public static final String DEXTERITY_PERK_TAGGED = "DexterityTag";
    public static final String DEXTERITY_PLAYER_SKILLS = "DexteritySkills";
    public static final String DEXTERITY_PLAYER_SKILLS_CURRENT_XP = "CurrentXp";
    public static final String DEXTERITY_PLAYER_SKILLS_TOTAL_XP = "TotalXp";
    public static final String DEXTERITY_PLAYER_SKILLS_TOTAL_LEVELS = "Levels";

    public static GenericPerk getGenericBlockBreakPerk(String name, AbstractToolSkill<?, ?> skill) {
        return PerkBuilder.<GenericPerk>of()
                .readyCondition((playerEntity, stack) -> skill.isToolCompatible(stack))
                .perkTrigger(InteractionTrigger.BREAK_BLOCK)
                .start((player, world, stack) -> {
                    float speed = ((SkillHandler) player).getSkillManager().getSkillEntry(skill).getLevel() >= 50 ? 0.8F : 0.6F;
                    player.getAttributeInstance(DexterityEntityAttributes.MINING_SPEED_PERK).addTemporaryModifier(new EntityAttributeModifier(DexterityData.MINING_SPEED_PERK_UUID, "Bonus Dexterity Mining Speed", speed, EntityAttributeModifier.Operation.ADDITION));
                    return ActionResult.PASS;
                })
                .end((player, world, stack) -> player.getAttributeInstance(DexterityEntityAttributes.MINING_SPEED_PERK).removeModifier(DexterityData.MINING_SPEED_PERK_UUID))
                .build(name, skill, level -> 200, level -> 20 + (level * 4));
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
