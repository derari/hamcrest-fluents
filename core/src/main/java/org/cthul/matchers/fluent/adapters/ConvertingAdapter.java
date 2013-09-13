package org.cthul.matchers.fluent.adapters;

import org.cthul.matchers.fluent.value.AbstractMatchValueAdapter;
import org.cthul.matchers.fluent.value.MatchValue;
import org.cthul.matchers.fluent.value.MatchValue.Element;
import org.cthul.matchers.fluent.value.MatchValue.ElementMatcher;
import org.cthul.matchers.fluent.value.MatchValue.ExpectationDescription;
import org.hamcrest.Description;
import org.hamcrest.SelfDescribing;
import org.hamcrest.internal.ReflectiveTypeFinder;

/**
 * Base class for adapters that get a single value from the source value.
 */
public abstract class ConvertingAdapter<Value, Property> extends AbstractMatchValueAdapter<Value, Property> {

    protected static final ReflectiveTypeFinder ADAPTED_TYPE_FINDER = new ReflectiveTypeFinder("adaptValue", 1, 0);
    
    private final Class<?> valueType;

    public ConvertingAdapter(Class<?> valueType) {
        this.valueType = valueType;
    }
    
    protected ConvertingAdapter(ReflectiveTypeFinder typeFinder) {
        this.valueType = typeFinder.findExpectedType(getClass());
    }

    public ConvertingAdapter() {
        this(ADAPTED_TYPE_FINDER);
    }
    
    @Override
    public MatchValue<Property> adapt(MatchValue<? extends Value> v) {
        return new ConvertedMatchValue<>(valueType, v);
    }
    
    protected abstract Property adaptValue(Value v);
    
    protected class ConvertedMatchValue<V extends Value> extends AbstractAdaptedValue<V, Property> {

        public ConvertedMatchValue(Class<?> valueType, MatchValue<V> actualValue) {
            super(valueType, actualValue);
        }

        @Override
        protected Converted<Property> createItem(Element<V> key) {
            Property i = adaptValue(key.value());
            return new Converted<>(i);
        }

        @Override
        protected boolean matchSafely(Element<V> element, ElementMatcher<? super Property> matcher) {
            return matcher.matches(cachedItem(element));
        }

        @Override
        public void describeValue(Description description) {
            ConvertingAdapter.this.describeValue(getActualValue(), description);
        }

        @Override
        public void describeValueType(Description description) {
            ConvertingAdapter.this.describeValueType(getActualValue(), description);
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
        protected <I extends Element<V>> MatchValue.Result<I> matchResultSafely(I element, ElementMatcher<V> adaptedMatcher, ElementMatcher<? super Property> matcher) {
            final MatchValue.Result<?> mr = matcher.matchResult(cachedItem(element));
            return new ResultBase<I>(element, adaptedMatcher, mr.matched()) {
                @Override
                public void describeMatch(Description d) {
                    mr.getMatch().describeMatch(d);
                }
                @Override
                public void describeExpected(ExpectationDescription description) {
                    mr.getMismatch().describeExpected(description);
                }
                @Override
                public void describeMismatch(Description d) {
                    mr.getMismatch().describeMismatch(d);
                }
            };
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
