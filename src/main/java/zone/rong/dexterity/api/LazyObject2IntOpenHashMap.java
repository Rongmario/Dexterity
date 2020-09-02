package zone.rong.dexterity.api;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.util.function.IntSupplier;

/**
 * Edited {@link Object2IntOpenHashMap#getInt(Object)} as {@link LazyObject2IntOpenHashMap#getOrLazilyEvaluate(Object, IntSupplier)}
 *
 * Using an {@link IntSupplier} as a way to retrieve an evaluation lazily when a key doesn't exist in the map.
 */
public class LazyObject2IntOpenHashMap<K> extends Object2IntOpenHashMap<K> {

    public int getOrLazilyEvaluate(K k, IntSupplier evaluation) {
        if (k == null) {
            return containsNullKey ? value[n] : evaluation.getAsInt();
        }
        K curr;
        final K[] key = this.key;
        int pos;
        // The starting point.
        if ((curr = key[pos = (HashCommon.mix((k).hashCode())) & mask]) == null) {
            return evaluation.getAsInt();
        }
        if (k.equals(curr)) {
            return value[pos];
        }
        // There's always an unused entry.
        while (true) {
            if ((curr = key[pos = (pos + 1) & mask]) == null) {
                return evaluation.getAsInt();
            }
            if (k.equals(curr)) {
                return value[pos];
            }
        }
    }

}
