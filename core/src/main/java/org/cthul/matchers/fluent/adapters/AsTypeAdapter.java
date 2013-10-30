package org.cthul.matchers.fluent.adapters;

import org.hamcrest.Description;

/**
 *
 */
public class AsTypeAdapter<V> extends ConvertingAdapter<Object, V> {

    public static <V> AsTypeAdapter<V> as(Class<V> clazz) {
        return new AsTypeAdapter<>(clazz);
    }
    
    public static AsTypeAdapter<Boolean> asBoolean() {
        return as(Boolean.class);
    }
    
    public static AsTypeAdapter<Byte> asByte() {
        return as(Byte.class);
    }
    
    public static AsTypeAdapter<Float> asFloat() {
        return as(Float.class);
    }
    
    public static AsTypeAdapter<Double> asDouble() {
        return as(Double.class);
    }
    
    public static AsTypeAdapter<Integer> asInt() {
        return as(Integer.class);
    }
    
    public static AsTypeAdapter<Long> asLong() {
        return as(Long.class);
    }
    
    public static AsTypeAdapter<Short> asShort() {
        return as(Short.class);
    }
    
    public static AsTypeAdapter<String> asString() {
        return as(String.class);
    }
    
    public AsTypeAdapter(Class<?> clazz) {
        super(clazz);
    }

    @Override
    protected boolean hasDescription() {
        return false;
    }

    @Override
    protected V adaptValue(Object v) {
        return (V) v;
    }

    @Override
    public void describeTo(Description description) {
        description.appendValue(this);
    }
}
