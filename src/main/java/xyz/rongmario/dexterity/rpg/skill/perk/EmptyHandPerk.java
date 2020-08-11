package xyz.rongmario.dexterity.rpg.skill.perk;

import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import xyz.rongmario.dexterity.rpg.skill.types.Skill;
import xyz.rongmario.dexterity.rpg.skill.common.api.SkillHandler;
import xyz.rongmario.dexterity.rpg.skill.perk.api.InteractionTrigger;

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
public class EmptyHandPerk extends BasePerk {

    public static final Identifier PACKET = new Identifier("dexterity", "empty_hand_interact");

    EmptyHandPerk(String name, Skill<?> parentSkill, Int2IntFunction cooldown, Int2IntFunction duration, InteractionTrigger trigger, BiPredicate<PlayerEntity, ItemStack> readyCondition, PerkStart startAction, PerkEnd endAction) {
        super(name, parentSkill, cooldown, duration, readyCondition, trigger, startAction, endAction);
    }

    @Override
    public void registerExtra() {
        super.registerExtra();
        ServerSidePacketRegistry.INSTANCE.register(PACKET, (ctx, packet) -> ctx.getTaskQueue().execute(() -> ((SkillHandler) ctx.getPlayer()).getPerkManager().readyUp(this)));
    }

}
