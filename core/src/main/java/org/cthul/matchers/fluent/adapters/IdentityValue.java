package org.cthul.matchers.fluent.adapters;

import org.cthul.matchers.fluent.value.MatchValue;
import org.hamcrest.Description;
import org.hamcrest.SelfDescribing;

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
    
    private final String name;

    public IdentityValue() {
        super(Object.class);
        this.name = null;
    }

    public IdentityValue(String name) {
        super(Object.class);
        this.name = name;
    }

    @Override
    public MatchValue<Value> adapt(MatchValue<Value> value) {
        if (name == null) {
            // shortcut, this adapter adds nothing
            return value;
        } else {
            return super.adapt(value);
        }
    }

    @Override
    protected boolean hasDescription() {
        return name != null;
    }

    @Override
    public void describeTo(Description description) {
        if (name == null) {
            description.appendText("identity");
        } else {
            description.appendText(name);
        }
    }

    @Override
    public void describeProducer(SelfDescribing producer, Description description) {
        if (name == null) {
            super.describeProducer(producer, description);
        } else {
            description.appendText(name);
        }
    }

    @Override
    public void describeConsumer(SelfDescribing consumer, Description description) {
//        if (name == null) {
            super.describeConsumer(consumer, description);
//        } else {
//            // FIXME
//            description.appendText(name);
//        }
    }

    @Override
    protected Value adaptValue(Value value) {
        return value;
    }
}
