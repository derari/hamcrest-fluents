package org.cthul.matchers.fluent.lib.collection;

import java.util.Map;
import org.cthul.matchers.fluent.adapters.SimpleAdapter;
import org.hamcrest.Factory;

/**
 *
 */
public class MapValueAdapter<V> extends SimpleAdapter<Map<?, ? extends V>, V> {
    
    @Factory
    public static <V> MapValueAdapter<V> value(Object key) {
        return new MapValueAdapter<>(key);
    }

    @Factory
    public static <V> MapValueAdapter<V> value(Object key, Class<V> clazz) {
        return new MapValueAdapter<>(key);
    }

    @Factory
    public static MapValueAdapter<Byte> byteValue(Object key) {
        return value(key);
    }

    @Factory
    public static MapValueAdapter<Boolean> booleanValue(Object key) {
        return value(key);
    }

    @Factory
    public static MapValueAdapter<Float> floatValue(Object key) {
        return value(key);
    }

    @Factory
    public static MapValueAdapter<Double> doubleValue(Object key) {
        return value(key);
    }

    @Factory
    public static MapValueAdapter<Integer> intValue(Object key) {
        return value(key);
    }

    @Factory
    public static MapValueAdapter<Long> longValue(Object key) {
        return value(key);
    }

    @Factory
    public static MapValueAdapter<Short> shortValue(Object key) {
        return value(key);
    }

    @Factory
    public static MapValueAdapter<String> stringValue(Object key) {
        return value(key);
    }

    private final Object key;

    public MapValueAdapter(Object key) {
        super("value <" + String.valueOf(key) + ">");
        this.key = key;
    }
    
    @Override
    protected V adaptValue(Map<?, ? extends V> v) {
        return v.get(key);
    }
}
