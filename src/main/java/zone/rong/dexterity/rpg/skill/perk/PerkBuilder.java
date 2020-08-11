package zone.rong.dexterity.rpg.skill.perk;

import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import zone.rong.dexterity.rpg.skill.perk.api.InteractionTrigger;
import zone.rong.dexterity.rpg.skill.types.Skill;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class PerkBuilder<P extends BasePerk> {

    protected boolean useHand = false;
    protected InteractionTrigger trigger;
    protected BiPredicate<PlayerEntity, ItemStack> readyCondition;
    protected BasePerk.PerkStart actionStart;
    protected BasePerk.PerkEnd actionEnd;

    public static <P extends BasePerk> PerkBuilder<P> of() {
        return new PerkBuilder<>();
    }

    public PerkBuilder<P> readyCondition(Predicate<PlayerEntity> condition) {
        this.readyCondition = (player, stack) -> condition.test(player);
        return this;
    }

    public PerkBuilder<P> readyCondition(BiPredicate<PlayerEntity, ItemStack> condition) {
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

    public PerkBuilder<P> start(BasePerk.PerkStart start) {
        this.actionStart = start;
        return this;
    }

    public PerkBuilder<P> end(BasePerk.PerkEnd end) {
        this.actionEnd = end;
        return this;
    }

    @SuppressWarnings("unchecked")
    public P build(String name, Skill parentSkill, Int2IntFunction cooldown, Int2IntFunction duration) {
        if (useHand) {
            return (P) new EmptyHandPerk(name, parentSkill, cooldown, duration, trigger, readyCondition, actionStart, actionEnd);
        }
        return (P) new GenericPerk(name, parentSkill, cooldown, duration, trigger, readyCondition, actionStart, actionEnd);
    }

}
