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

package zone.rong.dexterity.rpg.skill.perk;

import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import zone.rong.dexterity.rpg.skill.perk.api.InteractionTrigger;
import zone.rong.dexterity.rpg.skill.types.Skill;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class PerkBuilder<P extends Perk> {

    protected boolean useHand = false;
    protected InteractionTrigger trigger;
    protected BiPredicate<ServerPlayerEntity, ItemStack> readyCondition;
    protected Perk.PerkStart actionStart;
    protected Perk.PerkEnd actionEnd;

    public static <P extends Perk> PerkBuilder<P> of() {
        return new PerkBuilder<>();
    }

    public PerkBuilder<P> readyCondition(Predicate<ServerPlayerEntity> condition) {
        this.readyCondition = (player, stack) -> condition.test(player);
        return this;
    }

    public PerkBuilder<P> readyCondition(BiPredicate<ServerPlayerEntity, ItemStack> condition) {
        this.readyCondition = condition;
        return this;
    }

    public PerkBuilder<P> useHand() {
        this.useHand = true;
        return this;
    }

    public PerkBuilder<P> perkTrigger(InteractionTrigger trigger) {
        this.trigger = trigger;
        return this;
    }

    public PerkBuilder<P> start(Perk.PerkStart start) {
        this.actionStart = start;
        return this;
    }

    public PerkBuilder<P> end(Perk.PerkEnd end) {
        this.actionEnd = end;
        return this;
    }

    @SuppressWarnings("unchecked")
    public P build(String namespace, String path, Skill parentSkill, Int2IntFunction cooldown, Int2IntFunction duration) {
        if (useHand) {
            return (P) new EmptyHandPerk(namespace, path, parentSkill, cooldown, duration, readyCondition, trigger, actionStart, actionEnd);
        }
        return (P) new Perk(namespace, path, parentSkill, cooldown, duration, readyCondition, trigger, actionStart, actionEnd);
    }

}
