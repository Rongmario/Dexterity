package xyz.rongmario.dexterity.api;

import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;

import java.util.Collections;
import java.util.Set;

public class DexterityLootCondition implements LootCondition {

    private final DexterityPredicate predicate;

    public DexterityLootCondition(DexterityPredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return Collections.singleton(LootContextParameters.TOOL);
    }

    @Override
    public LootConditionType getType() {
        return null; // We don't serialize/de-serialize via json
    }

    @Override
    public boolean test(LootContext lootContext) {
        return predicate.test(lootContext.get(LootContextParameters.THIS_ENTITY), lootContext.get(LootContextParameters.TOOL), lootContext.get(LootContextParameters.POSITION));
    }

}
