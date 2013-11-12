package org.cthul.matchers.fluent.lib.collection;

import java.lang.reflect.Array;
import org.cthul.matchers.fluent.adapters.SimpleAdapter;
import org.cthul.matchers.fluent.value.MatchValue;
import org.cthul.matchers.fluent.value.MatchValueAdapter;
import org.hamcrest.Description;
import org.hamcrest.Factory;

public class ArraySizeAdapter extends SimpleAdapter<Object, Integer> {
    
    private static final ArraySizeAdapter INSTANCE = new ArraySizeAdapter();
    
    @Factory
    public static ArraySizeAdapter arraySize() {
        return INSTANCE;
    }
    
    @Factory
    public static MatchValue<Integer> arraySize(Object array) {
        return arraySize().adapt(array);
    }

    @Factory
    public static MatchValue<Integer> arraySizeOf(MatchValue<?> value) {
        return arraySize().adapt(value);
    }

    //@Factory
    public static <V> MatchValueAdapter<V, Integer> arraySizeOf(MatchValueAdapter<V, ?> adapter) {
        return arraySize().adapt(adapter);
    }

    public ArraySizeAdapter() {
        super("size", Object.class);
    }

    @Override
    protected boolean acceptValue(Object value) {
        return value.getClass().getComponentType() != null;
    }

    @Override
    protected void describeExpectedToAccept(Object value, Description description) {
        description.appendText("an array");
    }

    @Override
    protected void describeMismatchOfUnaccapted(Object value, Description description) {
        description.appendValue(value)
                .appendText(" was not an array");
    }
    
    @Override
    protected Integer adaptValue(Object v) {
        return Array.getLength(v);
    }
}
