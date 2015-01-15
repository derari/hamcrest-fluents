package org.cthul.matchers.fluent.lib.collection;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.cthul.matchers.fluent.adapters.Getter;
import org.cthul.matchers.fluent.adapters.LambdaAdapter;
import org.cthul.matchers.fluent.value.MatchValue;
import org.cthul.matchers.fluent.value.MatchValueAdapter;
import org.hamcrest.Factory;

public class MapAdapters {
    
    private static final MatchValueAdapter<Map<?,?>, Integer> MAP_SIZE_ADAPTER = new LambdaAdapter<>("size", new Getter<Map<?,?>, Integer>() {
        @Override
        public Integer get(Map<?, ?> value) {
            return value.size();
        }
    });
            
    private static final MatchValueAdapter<Map<?,?>, Set<?>> MAP_KEYS_ADAPTER = new LambdaAdapter<>("keys", new Getter<Map<?,?>, Set<?>>() {
        @Override
        public Set<?> get(Map<?, ?> value) {
            return value.keySet();
        }
    });
            
    private static final MatchValueAdapter<Map<?,?>, Collection<?>> MAP_VALUES_ADAPTER = new LambdaAdapter<>("values", new Getter<Map<?,?>, Collection<?>>() {
        @Override
        public Collection<?> get(Map<?, ?> value) {
            return value.values();
        }
    });
            
    public static MatchValueAdapter<Map<?,?>, Integer> mapSize() {
        return MAP_SIZE_ADAPTER;
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

    @Factory
    public static <V> MatchValueAdapter<V, Integer> mapSizeOf(MatchValueAdapter<V, ? extends Map<?, ?>> adapter) {
        return mapSize().adapt(adapter);
    }
    
    @Factory
    public static <K> MatchValueAdapter<Map<K,?>, Set<K>> mapKeys() {
        return (MatchValueAdapter) MAP_KEYS_ADAPTER;
    }

    @Factory
    public static <V> MatchValueAdapter<Map<?,V>, Collection<V>> mapValues() {
        return (MatchValueAdapter) MAP_VALUES_ADAPTER;
    }

    @Factory
    public static <V> MatchValueAdapter<Map<?, ? extends V>, V> value(final Object key) {
        Getter<Map<?, ? extends V>, V> getter = new Getter<Map<?, ? extends V>, V>() {
            @Override
            public V get(Map<?, ? extends V> value) {
                return value.get(key);
            }
        };
        return new LambdaAdapter("value <" + String.valueOf(key) + ">", getter);
    }

    @Factory
    public static MatchValueAdapter<Map<?, ? extends Byte>,Byte> byteValue(Object key) {
        return value(key);
    }

    @Factory
    public static MatchValueAdapter<Map<?, ? extends Boolean>,Boolean> booleanValue(Object key) {
        return value(key);
    }

    @Factory
    public static MatchValueAdapter<Map<?, ? extends Float>,Float> floatValue(Object key) {
        return value(key);
    }

    @Factory
    public static MatchValueAdapter<Map<?, ? extends Double>,Double> doubleValue(Object key) {
        return value(key);
    }

    @Factory
    public static MatchValueAdapter<Map<?, ? extends Integer>,Integer> intValue(Object key) {
        return value(key);
    }

    @Factory
    public static MatchValueAdapter<Map<?, ? extends Long>,Long> longValue(Object key) {
        return value(key);
    }

    @Factory
    public static MatchValueAdapter<Map<?, ? extends Short>,Short> shortValue(Object key) {
        return value(key);
    }

    @Factory
    public static MatchValueAdapter<Map<?, ? extends String>,String> stringValue(Object key) {
        return value(key);
    }
}
