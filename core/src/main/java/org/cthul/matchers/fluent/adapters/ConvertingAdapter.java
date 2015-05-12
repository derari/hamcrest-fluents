package org.cthul.matchers.fluent.adapters;

import org.cthul.matchers.fluent.value.ElementMatcher.Element;
import org.cthul.matchers.fluent.value.*;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.SelfDescribing;
import org.hamcrest.internal.ReflectiveTypeFinder;

/**
 * Base class for adapters that get a single value from the source value.
 */
public abstract class ConvertingAdapter<Value, Property> extends AbstractMatchValueAdapter<Value, Property> {

    protected static final ReflectiveTypeFinder ADAPTED_TYPE_FINDER = new ReflectiveTypeFinder("adaptValue", 1, 0);
    
    protected final Class<?> valueType;
    private final Matcher<Object> precondition;

    public ConvertingAdapter(Class<?> valueType) {
        this.valueType = valueType;
        this.precondition = null;
    }
    
    protected ConvertingAdapter(ReflectiveTypeFinder typeFinder) {
        this.valueType = typeFinder.findExpectedType(getClass());
        this.precondition = null;
    }

    public ConvertingAdapter() {
        this(ADAPTED_TYPE_FINDER);
    }

    public ConvertingAdapter(Matcher<? super Value> precondition) {
        this.valueType = null;
        this.precondition = (Matcher) precondition;
    }
    
    protected Matcher<Object> precondition() {
        return precondition;
    }
    
    @Override
    public MatchValue<Property> adapt(MatchValue<? extends Value> v) {
        return new ConvertedMatchValue<>(valueType, precondition(), v);
    }
    
    protected abstract Property adaptValue(Value v);
    
    protected class ConvertedMatchValue<V extends Value> extends AbstractAdaptedValue<V, Property> {

        public ConvertedMatchValue(Class<?> valueType, Matcher<Object> precondition, MatchValue<V> actualValue) {
            super(valueType, precondition, actualValue);
        }

        @Override
        protected Converted<Property> createItem(Element<V> key) {
            Property i = adaptValue(key.value());
            return new Converted<>(i);
        }

        @Override
        protected boolean matchSafely(Element<V> element, ElementMatcher<? super Property> matcher) {
            return matcher.matches((Element) cachedItem(element));
        }

        @Override
        public void describeValue(Description description) {
            ConvertingAdapter.this.describeValue(getSourceValue(), description);
        }

        @Override
        public void describeValueType(Description description) {
            ConvertingAdapter.this.describeValueType(getSourceValue(), description);
        }

        @Override
        protected void describeProducer(SelfDescribing sd, Description d) {
            ConvertingAdapter.this.describeProducer(sd, d);
        }

        @Override
        protected void describeConsumer(SelfDescribing sd, Description d) {
            ConvertingAdapter.this.describeConsumer(sd, d);
        }

        @Override
        protected ElementMatcher.Result matchResultSafely(Element<V> element, ElementMatcher<? super Property> matcher) {
            return matcher.matchResult((Element) cachedItem(element));
        }
    }
    
    protected static class Converted<Value> implements Element<Value> {
        
        private final Value value;

        public Converted(Value value) {
            this.value = value;
        }

        @Override
        public Value value() {
            return value;
        }
    }
}
