package org.cthul.matchers.fluent.lib.object;

import org.cthul.matchers.fluent.adapters.SimpleAdapter;
import org.cthul.matchers.fluent.value.MatchValue;
import org.cthul.matchers.fluent.value.MatchValueAdapter;
import org.hamcrest.Factory;

/**
 *
 */
public class ThrowableCauseAdapter extends SimpleAdapter<Throwable, Throwable> {

    private static final ThrowableCauseAdapter INSTANCE = new ThrowableCauseAdapter();
    
    @Factory
    public static ThrowableCauseAdapter cause() {
        return INSTANCE;
    }
    
    @Factory
    public static MatchValue<Throwable> causeOf(Throwable throwable) {
        return cause().adapt(throwable);
    }
    
    @Factory
    public static MatchValue<Throwable> causeOf(MatchValue<? extends Throwable> value) {
        return cause().adapt(value);
    }
    
    @Factory
    public static <V> MatchValueAdapter<V, Throwable> causeOf(MatchValueAdapter<V, ? extends Throwable> adapter) {
        return cause().adapt(adapter);
    }
    
    public ThrowableCauseAdapter() {
        super("cause", Throwable.class);
    }
    
    @Override
    protected Throwable adaptValue(Throwable v) {
        return v.getCause();
    }
}
