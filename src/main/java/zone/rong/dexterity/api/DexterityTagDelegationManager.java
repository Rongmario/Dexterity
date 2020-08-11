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

package zone.rong.dexterity.api;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.tag.Tag;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Fuck generics, all my homies hate generics
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class DexterityTagDelegationManager {

    private final Map<Set, Collection<Tag>> setDelegates = new Object2ObjectOpenHashMap<>();
    private final Map<Map, Collection<Pair<Tag, Integer>>> mapDelegates = new Object2ObjectOpenHashMap<>();

    public static DexterityTagDelegationManager ofSets(Set<Set> sets) {
        return new DexterityTagDelegationManager(sets, Collections.emptySet());
    }

    public static DexterityTagDelegationManager ofMaps(Set<Map> maps) {
        return new DexterityTagDelegationManager(Collections.emptySet(), maps);
    }

    public static DexterityTagDelegationManager of(Set<Set> sets, Set<Map> maps) {
        return new DexterityTagDelegationManager(sets, maps);
    }

    public static DexterityTagDelegationManager ofSingletons(Set set, Map map) {
        return new DexterityTagDelegationManager(Collections.singleton(set), Collections.singleton(map));
    }

    private DexterityTagDelegationManager(Set<Set> sets, Set<Map> maps) {
        for (Set set : sets) {
            this.setDelegates.put(set, new ObjectOpenHashSet<>());
        }
        for (Map map : maps) {
            this.mapDelegates.put(map, new ObjectOpenHashSet<>());
        }
    }

    public void setDelegate(Set setReference, Tag tagReference) {
        this.setDelegates.get(setReference).add(tagReference);
    }

    public void mapDelegate(Map mapReference, Tag tagReference, int amount) {
        this.mapDelegates.get(mapReference).add(Pair.of(tagReference, amount));
    }

    public void mapDelegate(Map mapReference, Map.Entry<Tag, Integer> entryReference) {
        this.mapDelegates.get(mapReference).add(Pair.of(entryReference.getKey(), entryReference.getValue()));
    }

    public void mapDelegate(Map mapReference, Map<Tag<?>, Integer> entryReference) {
        Collection<Pair<Tag, Integer>> mapValue = this.mapDelegates.get(mapReference);
        entryReference.forEach((key, value) -> mapValue.add(Pair.of(key, value)));
    }

    public void callback() {
        this.setDelegates.forEach((set, tags) -> tags.stream().map(Tag::values).forEach(set::addAll));
        this.mapDelegates.forEach((map, tags) -> tags.forEach(pair -> pair.getKey().values().forEach(tagValue -> map.put(tagValue, pair.getValue()))));
    }

}
