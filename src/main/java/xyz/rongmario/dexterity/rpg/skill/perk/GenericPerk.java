package xyz.rongmario.dexterity.rpg.skill.perk;

import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import xyz.rongmario.dexterity.rpg.skill.types.Skill;
import xyz.rongmario.dexterity.rpg.skill.common.api.SkillHandler;
import xyz.rongmario.dexterity.rpg.skill.perk.api.InteractionTrigger;

import java.util.function.BiPredicate;

public class GenericPerk extends BasePerk {

    protected final UseItemCallback callbackStart;

    GenericPerk(String name, Skill<?> parentSkill, Int2IntFunction cooldown, Int2IntFunction duration, InteractionTrigger trigger, BiPredicate<PlayerEntity, ItemStack> readyCondition, PerkStart actionStart, PerkEnd actionEnd) {
        super(name, parentSkill, cooldown, duration, readyCondition, trigger, actionStart, actionEnd);
        this.callbackStart = trigger == InteractionTrigger.NONE ? null : (player, world, hand) -> {
            if (world.isClient || hand == Hand.OFF_HAND || player.isSpectator() || player.isCreative()) {
                return TypedActionResult.pass(player.getStackInHand(hand));
            } else {
                ((SkillHandler) player).getPerkManager().readyUp(this);
            }
            return TypedActionResult.pass(player.getMainHandStack());
        };
    }

    @Override
    public void registerExtra() {
        super.registerExtra();
        UseItemCallback.EVENT.register(callbackStart);
    }

    public UseItemCallback getCallback() {
        return callbackStart;
    }

}
