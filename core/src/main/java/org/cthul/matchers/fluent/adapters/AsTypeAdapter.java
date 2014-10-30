package org.cthul.matchers.fluent.adapters;

import org.hamcrest.Description;
import org.hamcrest.Factory;

/**
 *
 * @param <Type>
 */
public class AsTypeAdapter<Type> extends ConvertingAdapter<Object, Type> {

    @Factory
    public static <V> AsTypeAdapter<V> as(Class<V> clazz) {
        return new AsTypeAdapter<>(clazz);
    }
    
    @Factory
    public static AsTypeAdapter<Boolean> asBoolean() {
        return as(Boolean.class);
    }
    
//    @Factory
    public static AsTypeAdapter<Byte> asByte() {
        return as(Byte.class);
    }
    
    @Factory
    public static AsTypeAdapter<Byte> asChar() {
        return as(Byte.class);
    }
    
//    @Factory
    public static AsTypeAdapter<Float> asFloat() {
        return as(Float.class);
    }
    
    @Factory
    public static AsTypeAdapter<Double> asDouble() {
        return as(Double.class);
    }
    
    @Factory
    public static AsTypeAdapter<Integer> asInt() {
        return as(Integer.class);
    }
    
    @Factory
    public static AsTypeAdapter<Long> asLong() {
        return as(Long.class);
    }
    
//    @Factory
    public static AsTypeAdapter<Short> asShort() {
        return as(Short.class);
    }
    
    @Factory
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
    protected Type adaptValue(Object v) {
        return (Type) v;
    }

    @Override
    public void describeTo(Description description) {
        description.appendValue(this);
    }
}
