package org.cthul.matchers.fluent.adapters;

import org.cthul.matchers.fluent.value.MatchValue;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 *
 */
public class IdentityValue<Value> extends ConvertingAdapter<Value, Value> {

    private static final IdentityValue INSTANCE = new IdentityValue<>();
    
    public static <T> IdentityValue<T> value() {
        return INSTANCE;
    }
    
    public static <T> MatchValue<T> value(T value) {
        return INSTANCE.adapt(value);
    }
    
    public IdentityValue() {
        super(Object.class);
    }

    @Override
    public MatchValue<Value> adapt(MatchValue<? extends Value> value) {
        // shortcut, this adapter adds nothing
        return (MatchValue) value;
    }

    @Override
    public Matcher<Value> adapt(Matcher<? super Value> matcher) {
        // shortcut, this adapter adds nothing
        return (Matcher) matcher;
    }

    @Override
    protected boolean hasDescription() {
        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("identity");
    }

    @Override
    protected Value adaptValue(Value value) {
        return value;
    }
}
