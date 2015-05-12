package org.cthul.matchers.fluent.adapters;

import org.cthul.matchers.diagnose.QuickMatcherBase;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

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
    
    private final boolean isNumber;
    private final Class<?> clazz;
    
    public AsTypeAdapter(Class<?> clazz) {
        super(precondition(clazz));
        this.isNumber = Number.class.isAssignableFrom(clazz);
        this.clazz = clazz;
    }

    @Override
    protected boolean hasDescription() {
        return false;
    }

    @Override
    protected Type adaptValue(Object v) {
        if (isNumber && v != null) {
            Number n = (Number) v;
            if (clazz == Float.class) {
                v = n.floatValue();
            } else if (clazz == Double.class) {
                v = n.doubleValue();
            } else if (clazz == Integer.class) {
                v = n.intValue();
            } else if (clazz == Long.class) {
                v = n.longValue();
            }
        }
        return (Type) v;
    }

    @Override
    public void describeTo(Description description) {
        description.appendValue(this);
    }
    
    private static Matcher<Object> precondition(Class<?> clazz) {
        if (clazz == Float.class) {
            return new Precondition(Byte.class, Short.class, Integer.class, Long.class, Float.class);
        }
        if (clazz == Double.class) {
            return new Precondition(Number.class);
        }
        if (clazz == Integer.class) {
            return new Precondition(Byte.class, Short.class, Integer.class);
        }
        if (clazz == Long.class) {
            return new Precondition(Byte.class, Short.class, Integer.class, Long.class);
        }
        return new Precondition(clazz);
    }
    
    private static class Precondition extends QuickMatcherBase<Object> {

        private final Class[] types;

        public Precondition(Class... types) {
            this.types = types;
        }

        @Override
        public boolean matches(Object o) {
            if (o == null) return true;
            for (Class<?> c: types) {
                if (c.isInstance(o)) return true;
            }
            return false;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("is null or an instance of");
            boolean first = true;
            for (Class<?> c: types) {
                if (first) {
                    description.appendText(" ");
                    first = false;
                } else {
                    description.appendText(" or ");
                }
                String n = c.getCanonicalName();
                if (types.length > 1) n = n.replaceFirst("java.lang.", "");
                description.appendText(n);
            }
        }

        @Override
        public void describeMismatch(Object item, Description description) {
            description.appendValue(item)
                    .appendText(" is a ")
                    .appendText(item.getClass().getCanonicalName());
        }
    }
}
