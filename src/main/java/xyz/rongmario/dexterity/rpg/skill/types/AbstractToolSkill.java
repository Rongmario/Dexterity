package xyz.rongmario.dexterity.rpg.skill.types;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.Tag;
import net.minecraft.util.registry.Registry;
import xyz.rongmario.dexterity.api.DexterityTagDelegationManager;

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
