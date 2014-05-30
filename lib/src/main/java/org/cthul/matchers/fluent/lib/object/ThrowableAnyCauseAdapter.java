package org.cthul.matchers.fluent.lib.object;

import java.util.ArrayList;
import java.util.List;
import org.cthul.matchers.fluent.adapters.SimpleAnyOfAdapter;
import org.cthul.matchers.fluent.value.MatchValue;
import org.cthul.matchers.fluent.value.MatchValueAdapter;
import org.hamcrest.Factory;

/**
 *
 */
public class ThrowableAnyCauseAdapter extends SimpleAnyOfAdapter<Throwable, Throwable> {

    private static final ThrowableAnyCauseAdapter INSTANCE = new ThrowableAnyCauseAdapter();
    
    @Factory
    public static ThrowableAnyCauseAdapter anyCause() {
        return INSTANCE;
    }
    
    @Factory
    public static ThrowableAnyCauseAdapter anyCauseOf() {
        return INSTANCE;
    }
    
    @Factory
    public static MatchValue<Throwable> anyCauseOf(Throwable throwable) {
        return anyCause().adapt(throwable);
    }
    
    @Factory
    public static MatchValue<Throwable> anyCauseOf(MatchValue<? extends Throwable> value) {
        return anyCause().adapt(value);
    }
    
    @Factory
    public static <V> MatchValueAdapter<V, Throwable> anyCauseOf(MatchValueAdapter<V, ? extends Throwable> adapter) {
        return anyCause().adapt(adapter);
    }
    
    public ThrowableAnyCauseAdapter() {
        super("any cause", Throwable.class);
    }

    @Override
    protected Iterable<? extends Throwable> getElements(Throwable value) {
        List<Throwable> list = new ArrayList<>();
        value = value.getCause();
        while (value != null) {
            list.add(value);
            value = value.getCause();
        }
        return list;
    }
}
