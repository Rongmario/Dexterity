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

package zone.rong.dexterity.rpg.skill.types;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.Tag;
import net.minecraft.util.registry.Registry;
import zone.rong.dexterity.api.DexterityTagDelegationManager;

import java.util.Set;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class AbstractToolSkill<S extends Skill<S>, A> extends Skill<S> {

    protected final Set<Item> requiredTools = new ObjectOpenHashSet<>();
    protected final Object2IntMap<A> affectedEntries = new Object2IntOpenHashMap<>();

    protected final DexterityTagDelegationManager manager = DexterityTagDelegationManager.ofSingletons(this.requiredTools, this.affectedEntries);

    protected AbstractToolSkill(String path, ItemConvertible displayItem, int colour) {
        super(path, displayItem, colour);
    }

    protected AbstractToolSkill(String namespace, String path, ItemConvertible displayItem, int colour) {
        super(namespace, path, displayItem, colour);
    }

    public S addTool(Item item) {
        this.requiredTools.add(item);
        return self();
    }

    public S addTool(ItemStack stack) {
        this.requiredTools.add(stack.getItem());
        return self();
    }

    public S addTool(Tag<Item> tag) {
        manager.setDelegate(this.requiredTools, tag);
        return self();
    }

    public S addTool(Predicate<Item> predicate) {
        Registry.ITEM.stream().filter(predicate).forEach(requiredTools::add);
        return self();
    }

    public boolean isToolCompatible(ItemStack stack) {
        return this.requiredTools.contains(stack.getItem());
    }

    public DexterityTagDelegationManager getManager() {
        return manager;
    }

    public Set<Item> getRequiredTools() {
        return requiredTools;
    }

    public int getXp(A entry) {
        return this.affectedEntries.getOrDefault(entry, 1);
    }

    public S addEntries(UnaryOperator<ImmutableMap.Builder<A, Integer>> entries) {
        this.affectedEntries.putAll(entries.apply(new ImmutableMap.Builder<>()).build());
        return self();
    }

    public S addTagEntries(UnaryOperator<ImmutableMap.Builder<Tag<?>, Integer>> entries) { // Tag<A> but shit is annoying
        manager.mapDelegate(this.affectedEntries, entries.apply(new ImmutableMap.Builder<>()).build());
        return self();
    }

}
