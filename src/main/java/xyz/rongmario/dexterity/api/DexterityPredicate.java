package xyz.rongmario.dexterity.api;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import xyz.rongmario.dexterity.rpg.skill.perk.BasePerk;
import xyz.rongmario.dexterity.rpg.skill.common.api.ServerWorldArtificialBlockStatesHandler;
import xyz.rongmario.dexterity.rpg.skill.common.api.SkillHandler;

public class DexterityPredicate {

    private final BasePerk perk;
    private final boolean hasToBeNatural;

    private DexterityPredicate(BasePerk perk, boolean hasToBeNatural) {
        this.perk = perk;
        this.hasToBeNatural = hasToBeNatural;
    }

    public boolean test(Entity entity, ItemStack stack, BlockPos pos) {
        boolean toReturn = false;
        if (entity instanceof ServerPlayerEntity) {
            if (hasToBeNatural) {
                toReturn = !((ServerWorldArtificialBlockStatesHandler) entity.world).isArtificial(pos);
            }
            if (perk != null) {
                SkillHandler player = (SkillHandler) entity;
                toReturn = player.getPerkManager().isPerkActive(perk) && player.getPerkManager().isPerkActive(stack) &&
                        ((ServerPlayerEntity) entity).getRandom().nextInt(1000) <= player.getSkillManager().getSkillEntry(perk.getParentSkill()).getLevel();
            }
        }
        return toReturn;
    }

    public static class Builder {

        private BasePerk perk = null;
        private boolean hasToBeNatural = false;

        public Builder perk(BasePerk perk) {
            this.perk = perk;
            return this;
        }

        public Builder natural() {
            this.hasToBeNatural = true;
            return this;
        }

        public DexterityPredicate build() {
            return new DexterityPredicate(perk, hasToBeNatural);
        }

    }

}
