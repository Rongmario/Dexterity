package zone.rong.dexterity.api.skill;

import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.registries.ForgeRegistryEntry;
import zone.rong.dexterity.Dexterity;
import zone.rong.dexterity.api.LazyObject2IntOpenHashMap;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class SkillType extends ForgeRegistryEntry<SkillType> {

    private final TextFormatting colour;
    private final Supplier<ItemStack> associatedItem;
    private final TranslationTextComponent name, description;
    private final Object2ObjectMap<Class<?>, LazyObject2IntOpenHashMap<?>> lazyXPQuery;
    private final Object2ObjectMap<Class<?>, Object2IntMap<Predicate<?>>> xpPredicateMatching;
    private final Object2ObjectMap<Class<?>, Collection<?>> compatibleToolsQuery;
    private final Object2ObjectMap<Class<?>, Collection<Predicate<?>>> compatibleToolPredicateMatching;

    public SkillType(String id, TextFormatting colour, IItemProvider associatedItem) {
        this(Dexterity.MOD_ID, id, colour, associatedItem);
    }

    public SkillType(String domain, String id, TextFormatting colour, IItemProvider associatedItem) {
        this.name = new TranslationTextComponent("skill.dexterity." + domain + "_" + id);
        this.description = new TranslationTextComponent("skill.dexterity." + domain + "_" + id);
        this.colour = colour;
        this.associatedItem = () -> new ItemStack(associatedItem);
        this.lazyXPQuery = new Object2ObjectOpenHashMap<>();
        this.xpPredicateMatching = new Object2ObjectOpenHashMap<>();
        this.compatibleToolsQuery = new Object2ObjectOpenHashMap<>();
        this.compatibleToolPredicateMatching = new Object2ObjectOpenHashMap<>();
        setRegistryName(domain, id);
    }

    public TranslationTextComponent getName() {
        return name;
    }

    public TranslationTextComponent getDescription() {
        return description;
    }

    // TODO: change in 1.16+
    public TextFormatting getColour() {
        return colour;
    }

    public Supplier<ItemStack> getAssociatedItem() {
        return associatedItem;
    }

    public <XP> int getXP(Class<XP> entryClass, XP entry) {
        if (!this.lazyXPQuery.containsKey(entryClass)) {
            return getMatchingXP(entryClass, entry);
        }
        LazyObject2IntOpenHashMap<XP> query = (LazyObject2IntOpenHashMap<XP>) this.lazyXPQuery.get(entryClass);
        return query.getOrLazilyEvaluate(entry, () -> getMatchingXP(entryClass, entry));
    }

    private <XP> int getMatchingXP(Class<XP> entryClass, XP entry) {
        Object2IntMap<Predicate<XP>> map = (Object2IntMap<Predicate<XP>>) (Object2IntMap<?>) this.xpPredicateMatching.get(entryClass);
        return map.object2IntEntrySet().stream().filter(e -> e.getKey().test(entry)).mapToInt(Object2IntMap.Entry::getIntValue).max().orElse(0);
    }

    public <XP> void addXP(Class<XP> entryClass, XP entry, int amount) {
        if (ModLoadingContext.get().getActiveContainer().getCurrentState().ordinal() <= 9) {
            throw new IllegalStateException("Cannot add more XP entries in an unmodifiable state!");
        }
        ((LazyObject2IntOpenHashMap<XP>) this.lazyXPQuery.computeIfAbsent(entryClass, k -> new LazyObject2IntOpenHashMap<>())).put(entry, amount);
    }

    public <XP> void addXP(Class<XP> entryClass, Predicate<XP> entry, int amount) {
        if (ModLoadingContext.get().getActiveContainer().getCurrentState().ordinal() <= 9) {
            throw new IllegalStateException("Cannot add more XP entries in an unmodifiable state!");
        }
        this.xpPredicateMatching.computeIfAbsent(entryClass, key -> new Object2IntOpenHashMap<>()).put(entry, amount);
    }

    public <C> boolean isCompatible(Class<C> entryClass, C entry) {
        if (!this.compatibleToolsQuery.containsKey(entryClass)) {
            return this.compatibleToolPredicateMatching.containsKey(entryClass) && ((Collection<Predicate<C>>) (Collection<?>) this.compatibleToolPredicateMatching.get(entryClass)).stream().anyMatch(p -> p.test(entry));
        }
        return this.compatibleToolsQuery.get(entryClass).contains(entry) || this.compatibleToolPredicateMatching.containsKey(entryClass) && ((Collection<Predicate<C>>) (Collection<?>) this.compatibleToolPredicateMatching.get(entryClass)).stream().anyMatch(p -> p.test(entry));
    }

    public <C> void addCompatibility(Class<C> entryClass, C entry) {
        ((Collection<C>) this.compatibleToolsQuery.computeIfAbsent(entryClass, k -> new ObjectOpenHashSet<>())).add(entry);
    }

    public <C> void addCompatibility(Class<C> entryClass, Predicate<C> entry) {
        this.compatibleToolPredicateMatching.computeIfAbsent(entryClass, k -> new ObjectOpenHashSet<>()).add(entry);
    }

}
