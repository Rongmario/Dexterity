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
