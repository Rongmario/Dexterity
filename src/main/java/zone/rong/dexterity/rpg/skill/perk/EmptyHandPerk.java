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
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import zone.rong.dexterity.rpg.skill.perk.api.InteractionTrigger;
import zone.rong.dexterity.rpg.skill.types.Skill;

import java.util.function.BiPredicate;

/**
 * Allows processing of perks that occur when {@link PlayerEntity#getMainHandStack()#isEmpty()}
 *
 * This is because {@link ClientPlayerInteractionManager#interactItem(PlayerEntity, World, Hand)}
 * does not fire when {@link ItemStack#isEmpty()} -> thus we need packets to save us from this hell.
 *
 * Primarily used for Unarmed perks -> alchemy and other things that do not reply on item tools may need it.
 * We could also make these 'passive' perks.
 */
@Deprecated
public class EmptyHandPerk extends Perk {

    public static final Identifier PACKET = new Identifier("dexterity", "empty_hand_interact");

    EmptyHandPerk(String namespace, String path, Skill<?> parentSkill, Int2IntFunction cooldown, Int2IntFunction duration, BiPredicate<ServerPlayerEntity, ItemStack> readyCondition, InteractionTrigger trigger, PerkStart startAction, PerkEnd endAction) {
        super(namespace, path, parentSkill, cooldown, duration, readyCondition, trigger, startAction, endAction);
    }

    @Override
    public void registerExtra() {
        // super.registerExtra();
        // ServerSidePacketRegistry.INSTANCE.register(PACKET, (ctx, packet) -> ctx.getTaskQueue().execute(() -> ((SkillHandler) ctx.getPlayer()).getPerkManager().readyUp(ItemStack.EMPTY)));
    }

}
