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

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import zone.rong.dexterity.DexterityData;
import zone.rong.dexterity.api.Unlockable;
import zone.rong.dexterity.rpg.skill.types.Skill;
import zone.rong.dexterity.rpg.skill.common.api.SkillHandler;
import zone.rong.dexterity.rpg.skill.perk.api.InteractionTrigger;

import java.util.function.BiPredicate;
import java.util.function.IntUnaryOperator;

public class Perk implements Unlockable {

    protected final String namespace, path;
    protected final Identifier identifier;
    protected final MutableText displayName;
    protected final Skill<?> parentSkill;
    protected final IntUnaryOperator levelApplyToCooldown, levelApplyToDuration;
    protected final BiPredicate<ServerPlayerEntity, ItemStack> readyCondition;
    protected final InteractionTrigger trigger;
    protected final PerkStart actionStart;
    protected final PerkEnd actionEnd;

    Perk(String namespace, String path, Skill<?> parentSkill, IntUnaryOperator cooldown, IntUnaryOperator duration, BiPredicate<ServerPlayerEntity, ItemStack> readyCondition, InteractionTrigger trigger, PerkStart start, PerkEnd end) {
        this.namespace = namespace;
        this.path = path;
        this.identifier = new Identifier(namespace, path);
        this.displayName = new TranslatableText("perk.dexterity.".concat(path));
        this.parentSkill = parentSkill;
        this.levelApplyToCooldown = cooldown;
        this.levelApplyToDuration = duration;
        this.readyCondition = readyCondition;
        this.trigger = trigger;
        this.actionStart = start;
        this.actionEnd = end;
        Registry.register(DexterityData.PERKS, identifier, this);
    }

    @Override
    public String toString() {
        return identifier.toString();
    }

    @Override
    public MutableText getName() {
        return displayName;
    }

    @Override
    public int getColour() {
        return parentSkill.getColour();
    }

    @Override
    public ItemStack getDisplayItem() {
        return parentSkill.getDisplayItem();
    }

    public Skill<?> getParentSkill() {
        return parentSkill;
    }

    public IntUnaryOperator getDuration() {
        return levelApplyToDuration;
    }

    public IntUnaryOperator getCooldown() {
        return levelApplyToCooldown;
    }

    public BiPredicate<ServerPlayerEntity, ItemStack> getReadyCondition() {
        return readyCondition;
    }

    public InteractionTrigger getTrigger() {
        return trigger;
    }

    public PerkStart getStartingAction() {
        return actionStart;
    }

    public PerkEnd getEndingAction() {
        return actionEnd;
    }

    public void registerExtra() {
        AttackBlockCallback.EVENT.register(this::setPerkActive);
        /*
        switch (trigger) {
            case NONE:
                break;
            case USE_BLOCK:
                UseBlockCallback.EVENT.register(this::setPerkActive);
                break;
            case USE_ENTITY:
                UseEntityCallback.EVENT.register(this::setPerkActive);
                break;
            case ATTACK_BLOCK:
                AttackBlockCallback.EVENT.register(this::setPerkActive);
                break;
            case ATTACK_ENTITY:
                AttackEntityCallback.EVENT.register(this::setPerkActive);
                break;
            case BREAK_BLOCK:
                ServerBlockBreakEvent.EVENT.register(this::setPerkActive);
                break;
            default:
                UseItemCallback.EVENT.register(this::setPerkActiveTyped);
                break;
        }
         */
    }

    private ActionResult setPerkActive(PlayerEntity player, World world, Hand hand, HitResult result) {
        if (world.isClient) {
            return ActionResult.PASS;
        }
        // return ((SkillHandler) player).getPerkManager().setPerkActive(this);
        return ((SkillHandler) player).getPerkManager().setPerkActive();
    }

    private ActionResult setPerkActive(PlayerEntity player, World world, Hand hand, BlockPos pos, BlockState state) {
        if (world.isClient) {
            return ActionResult.PASS;
        }
        // return ((SkillHandler) player).getPerkManager().setPerkActive(this);
        return ((SkillHandler) player).getPerkManager().setPerkActive();
    }

    private ActionResult setPerkActive(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction) {
        if (world.isClient) {
            return ActionResult.PASS;
        }
        // return ((SkillHandler) player).getPerkManager().setPerkActive(this);
        return ((SkillHandler) player).getPerkManager().setPerkActive();
    }

    private ActionResult setPerkActive(PlayerEntity player, World world, Hand hand, Entity entity, HitResult result) {
        if (world.isClient) {
            return ActionResult.PASS;
        }
        // return ((SkillHandler) player).getPerkManager().setPerkActive(this);
        return ((SkillHandler) player).getPerkManager().setPerkActive();
    }

    private TypedActionResult<ItemStack> setPerkActiveTyped(PlayerEntity player, World world, Hand hand) {
        if (world.isClient) {
            return TypedActionResult.pass(player.getMainHandStack());
        }
        // return new TypedActionResult<>(((SkillHandler) player).getPerkManager().setPerkActive(this), player.getMainHandStack());
        return new TypedActionResult<>(((SkillHandler) player).getPerkManager().setPerkActive(), player.getMainHandStack());
    }

    @FunctionalInterface
    public interface PerkStart {

        ActionResult start(PlayerEntity player, World world, ItemStack stack);

        default ActionResult callStart(Perk perk, PlayerEntity player, World world, ItemStack stack) {
            player.sendMessage(new LiteralText("||<- ").append(perk.displayName.shallowCopy().formatted(Formatting.BOLD, Formatting.BLUE)).append(new LiteralText(" ->||")), true);
            return start(player, world, stack);
        }

    }

    @FunctionalInterface
    public interface PerkEnd {

        void end(PlayerEntity player, World world, ItemStack stack);

        default void callEnd(Perk perk, PlayerEntity player, World world, ItemStack stack) {
            player.sendMessage(perk.getName().shallowCopy().append(new TranslatableText("message.dexterity.perk.diminish")).formatted(Formatting.AQUA), true);
            end(player, world, stack);
        }

    }

}
