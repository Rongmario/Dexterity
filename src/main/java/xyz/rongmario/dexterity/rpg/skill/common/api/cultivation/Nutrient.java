package xyz.rongmario.dexterity.rpg.skill.common.api.cultivation;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.Tag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import xyz.rongmario.dexterity.DexterityData;

public final class Nutrient implements Comparable<Nutrient>  {

    private final String namespace, path;
    private final Identifier identifier;
    protected final Text displayName;
    protected final Formatting colour;
    protected final ItemStack displayItem;
    protected final boolean appliesToAnimals;

    protected final ObjectSet<Item> nutritiousItems = new ObjectOpenHashSet<>();

    public Nutrient(String namespace, String path, Formatting colour, ItemConvertible displayItem, boolean appliesToAnimals) {
        this.namespace = namespace;
        this.path = path;
        this.identifier = new Identifier(namespace, path);
        this.displayName = new TranslatableText("nutrient.".concat(namespace).concat(".").concat(path)).formatted(colour);
        this.colour = colour;
        this.displayItem = new ItemStack(displayItem);
        this.appliesToAnimals = appliesToAnimals;
        Registry.register(DexterityData.NUTRIENTS, identifier, this);
    }

    public Nutrient add(Item item) {
        this.nutritiousItems.add(item);
        return this;
    }

    public Nutrient add(Tag<Item> tag) {
        this.nutritiousItems.addAll(tag.values());
        return this;
    }

    public boolean test(Item item) {
        return this.nutritiousItems.contains(item);
    }

    public boolean test(ItemStack stack) {
        return this.nutritiousItems.contains(stack.getItem());
    }

    @Override
    public String toString() {
        return identifier.toString();
    }

    public String getNamespace() {
        return namespace;
    }

    public String getPath() {
        return path;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public Text getDisplayName() {
        return displayName;
    }

    public Formatting getColour() {
        return colour;
    }

    public ItemStack getDisplayItem() {
        return displayItem;
    }

    public boolean appliesToAnimals() {
        return appliesToAnimals;
    }

    @Override
    public int compareTo(Nutrient otherNutrient) {
        return toString().compareTo(otherNutrient.toString());
    }
}
