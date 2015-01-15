package org.cthul.matchers.fluent.lib.collection;

import java.lang.reflect.Array;
import org.cthul.matchers.diagnose.QuickDiagnosingMatcherBase;
import org.cthul.matchers.fluent.adapters.SimpleAdapter;
import org.cthul.matchers.fluent.value.MatchValue;
import org.cthul.matchers.fluent.value.MatchValueAdapter;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

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

    @Factory
    public static <V> MatchValueAdapter<V, Integer> arraySizeOf(MatchValueAdapter<V, ?> adapter) {
        return arraySize().adapt(adapter);
    }

    public ArraySizeAdapter() {
        super("size", Object.class);
    }

    @Override
    protected Matcher<? super Object> precondition() {
        return new QuickDiagnosingMatcherBase<Object>() {
            @Override
            public boolean matches(Object item, Description mismatch) {
                if (item == null) {
                    mismatch.appendText("was <null>");
                    return false;
                } else if (!item.getClass().isArray()) {
                    mismatch.appendValue(item).appendText(" was not an array");
                    return false;
                }
                return true;
            }
            @Override
            public void describeTo(Description description) {
                description.appendText("an array");
            }
        };
    }
    
    @Override
    protected Integer adaptValue(Object v) {
        return Array.getLength(v);
    }
}
