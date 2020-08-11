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

package zone.rong.dexterity.api;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import zone.rong.dexterity.rpg.skill.perk.BasePerk;
import zone.rong.dexterity.rpg.skill.common.api.ServerWorldArtificialBlockStatesHandler;
import zone.rong.dexterity.rpg.skill.common.api.SkillHandler;

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
