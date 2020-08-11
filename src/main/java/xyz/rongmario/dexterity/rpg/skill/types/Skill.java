package xyz.rongmario.dexterity.rpg.skill.types;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import xyz.rongmario.dexterity.DexterityData;
import xyz.rongmario.dexterity.rpg.skill.perk.BasePerk;
import xyz.rongmario.dexterity.rpg.skill.perk.PerkBuilder;
import xyz.rongmario.dexterity.rpg.skill.trait.Trait;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class Skill<S extends Skill<S>> implements Comparable<S> {

    private final String namespace, path;
    private final Text displayName, description;
    private final Identifier identifier;
    private final ItemStack displayItem;
    private final TextColor colourObject;
    private final int colour;

    private final Set<BasePerk> perkLookup = new ObjectOpenHashSet<>();
    private final Set<Trait> traitLookup = new ObjectOpenHashSet<>();

    public Skill(String path, ItemConvertible displayItem, int colour) {
        this("dexterity", path, displayItem, colour);
    }

    public Skill(String namespace, String path, ItemConvertible displayItem, int colour) {
        this.namespace = namespace;
        this.path = path;
        final String localizationPrefix = "skills.".concat(namespace).concat(".");
        this.identifier = new Identifier(namespace, path);
        this.colour = colour;
        this.colourObject = TextColor.fromRgb(colour);
        this.displayName = new TranslatableText(localizationPrefix.concat(path)).styled(style -> style.withColor(this.colourObject));
        this.description = new TranslatableText(localizationPrefix.concat("description")).styled(style -> style.withColor(this.colourObject));
        this.displayItem = new ItemStack(displayItem);
        Registry.register(DexterityData.SKILLS, identifier, this);
    }

    public S addPerk(BiFunction<Skill<S>, PerkBuilder<?>, BasePerk> perkApplyFunction) {
        BasePerk perk = perkApplyFunction.apply(this, PerkBuilder.of());
        this.perkLookup.add(perk);
        return self();
    }

    public S addPerk(BasePerk perk) {
        this.perkLookup.add(perk);
        return self();
    }

    public S addTrait(int levelBarrier, Consumer<ServerPlayerEntity> serverPlayerConsumer) {
        this.traitLookup.add(new Trait(this, levelBarrier, serverPlayerConsumer));
        return self();
    }

    public S addTrait(Trait trait) {
        this.traitLookup.add(trait);
        return self();
    }

    protected S self() {
        return (S) this;
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

    public Text getDescription() {
        return description;
    }

    public Set<BasePerk> getPerks() {
        return perkLookup;
    }

    public Set<Trait> getTraitLookup() {
        return traitLookup;
    }

    public Text getDisplayName() {
        return displayName;
    }

    public int getColour() {
        return colour;
    }

    public TextColor getColourObject() {
        return colourObject;
    }

    public ItemStack getDisplayItem() {
        return displayItem;
    }

    @Override
    public int hashCode() {
        return this.identifier.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Skill && ((Skill) other).namespace.equals(this.namespace) && ((Skill) other).path.equals(this.path);
    }

    @Override
    public int compareTo(Skill otherSkill) {
        return toString().compareTo(otherSkill.toString());
    }

}
