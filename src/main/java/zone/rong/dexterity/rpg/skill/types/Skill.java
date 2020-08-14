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

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import zone.rong.dexterity.DexterityData;
import zone.rong.dexterity.api.Unlockable;
import zone.rong.dexterity.rpg.skill.perk.Perk;
import zone.rong.dexterity.rpg.skill.perk.PerkBuilder;
import zone.rong.dexterity.rpg.skill.trait.BaseTrait;
import zone.rong.dexterity.rpg.skill.trait.ModifyPlayerTrait;
import zone.rong.dexterity.rpg.skill.trait.Trait;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class Skill<S extends Skill<S>> implements Comparable<S>, Unlockable {

    private final String namespace, path;
    private final MutableText displayName, description;
    private final Identifier identifier;
    private final ItemStack displayItem;
    private final TextColor colourObject;
    private final int colour;

    private final Set<Perk> perkLookup = new ObjectOpenHashSet<>();
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

    public S addPerk(BiFunction<Skill<S>, PerkBuilder<?>, Perk> perkApplyFunction) {
        Perk perk = perkApplyFunction.apply(this, PerkBuilder.of());
        this.perkLookup.add(perk);
        return self();
    }

    public S addPerk(Perk perk) {
        this.perkLookup.add(perk);
        return self();
    }

    public S addTrait(String name, int levelToUnlock, Consumer<ServerPlayerEntity> playerConsumer) {
        this.traitLookup.add(new ModifyPlayerTrait(name, this, levelToUnlock, playerConsumer));
        return self();
    }

    public S addTrait(String name, int levelToUnlock) {
        this.traitLookup.add(new BaseTrait(name, this, levelToUnlock));
        return self();
    }

    public S addTrait(Trait trait) {
        this.traitLookup.add(trait);
        return self();
    }

    @SuppressWarnings("unchecked")
    protected S self() {
        return (S) this;
    }

    @Override
    public String toString() {
        return identifier.toString();
    }

    @Override
    public MutableText getName() {
        return displayName;
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

    public Set<Perk> getPerks() {
        return perkLookup;
    }

    public Set<Trait> getTraitLookup() {
        return traitLookup;
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
