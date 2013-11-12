package org.cthul.matchers.fluent.lib.collection;

import java.util.Collection;
import java.util.Map;
import org.cthul.matchers.fluent.adapters.SimpleAdapter;
import org.cthul.matchers.fluent.value.MatchValue;
import org.cthul.matchers.fluent.value.MatchValueAdapter;
import org.hamcrest.Factory;

public class MapSizeAdapter extends SimpleAdapter<Map<?, ?>, Integer> {
    
    private static final MapSizeAdapter INSTANCE = new MapSizeAdapter();
    
    @Factory
    public static MapSizeAdapter mapSize() {
        return INSTANCE;
    }
    
    @Factory
    public static MatchValue<Integer> sizeOf(Map<?, ?> iterable) {
        return mapSize().adapt(iterable);
    }

    @Factory
    public static MatchValue<Integer> mapSizeOf(Map<?, ?> iterable) {
        return mapSize().adapt(iterable);
    }

    @Factory
    public static MatchValue<Integer> mapSizeOf(MatchValue<? extends Map<?, ?>> value) {
        return mapSize().adapt(value);
    }

    //@Factory
    public static <V> MatchValueAdapter<V, Integer> mapSizeOf(MatchValueAdapter<V, ? extends Map<?, ?>> adapter) {
        return mapSize().adapt(adapter);
    }

    public MapSizeAdapter() {
        super("size", (Class) Map.class);
    }

    @Override
    protected Integer adaptValue(Map<?, ?> v) {
        return v.size();
    }
}
