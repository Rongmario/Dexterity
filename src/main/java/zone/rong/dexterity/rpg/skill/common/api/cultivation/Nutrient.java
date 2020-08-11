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

package zone.rong.dexterity.rpg.skill.common.api.cultivation;

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
import zone.rong.dexterity.DexterityData;

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
