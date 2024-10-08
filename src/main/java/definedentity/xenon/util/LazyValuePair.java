package definedentity.xenon.util;

import java.util.function.Function;
import net.minecraft.util.LazyLoadedValue;
import org.apache.commons.lang3.tuple.Pair;

/** Created by covers1624 on 5/1/20. */
public class LazyValuePair<K, V> extends Pair<K, V> {

    private final K key;
    private final LazyLoadedValue<V> value;

    public LazyValuePair(K key, Function<K, V> func) {
        this.key = key;
        value = new LazyLoadedValue<>(() -> func.apply(key));
    }

    public static <K, V> LazyValuePair<K, V> of(K key, Function<K, V> func) {
        return new LazyValuePair<>(key, func);
    }

    @Override
    public K getLeft() {
        return key;
    }

    @Override
    public V getRight() {
        return value.get();
    }

    @Override
    public V setValue(V value) {
        throw new UnsupportedOperationException();
    }
}
