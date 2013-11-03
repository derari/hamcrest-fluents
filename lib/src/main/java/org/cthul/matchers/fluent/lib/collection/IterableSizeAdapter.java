package org.cthul.matchers.fluent.lib.collection;

import java.util.Collection;
import org.cthul.matchers.fluent.adapters.SimpleAdapter;
import org.cthul.matchers.fluent.value.MatchValue;
import org.cthul.matchers.fluent.value.MatchValueAdapter;
import org.hamcrest.Factory;

public class IterableSizeAdapter extends SimpleAdapter<Iterable<?>, Integer> {
    
    private static final IterableSizeAdapter INSTANCE = new IterableSizeAdapter();
    
    @Factory
    public static IterableSizeAdapter size() {
        return INSTANCE;
    }
    
    @Factory
    public static MatchValue<Integer> sizeOf(Iterable<?> iterable) {
        return size().adapt(iterable);
    }

    @Factory
    public static MatchValue<Integer> sizeOf(MatchValue<? extends Iterable<?>> value) {
        return size().adapt(value);
    }

    @Factory
    public static <V> MatchValueAdapter<V, Integer> sizeOf(MatchValueAdapter<V, ? extends Iterable<?>> adapter) {
        return size().adapt(adapter);
    }

    public IterableSizeAdapter() {
        super("size", (Class) Iterable.class);
    }

    @Override
    protected Integer adaptValue(Iterable<?> v) {
        if (v instanceof Collection) {
            return ((Collection) v).size();
        }
        int i = 0;
        for (Object _: v) {
            i++;
        }
        return i;
    }
    
}
