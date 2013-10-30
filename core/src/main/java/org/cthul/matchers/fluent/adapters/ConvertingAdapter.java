package org.cthul.matchers.fluent.adapters;

import org.cthul.matchers.fluent.value.ElementMatcher.Element;
import org.cthul.matchers.fluent.value.ElementMatcher.ExpectationDescription;
import org.cthul.matchers.fluent.value.*;
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
    
    protected boolean acceptValue(Value value) {
        return true;
    }

    protected void describeExpectedToAccept(Value value, Description description) {
        description.appendText("a valid ")
                .appendText(valueType.getSimpleName());
    }

    protected void describeMismatchOfUnaccapted(Value value, Description description) {
        description.appendText("was ").appendValue(value);
    }
    
    protected class ConvertedMatchValue<V extends Value> extends AbstractAdaptedValue<V, Property> {

        public ConvertedMatchValue(Class<?> valueType, MatchValue<V> actualValue) {
            super(valueType, actualValue);
        }

        @Override
        protected boolean acceptValue(Object value) {
            if (super.acceptValue(value)) {
                return ConvertingAdapter.this.acceptValue((Value) value);
            }
            return false;
        }

        @Override
        protected void describeExpectedToAccept(Object value, Description description) {
            if (super.acceptValue(value)) {
                ConvertingAdapter.this.describeExpectedToAccept((Value) value, description);
            } else {
                super.describeExpectedToAccept(value, description);
            }
        }

        @Override
        protected void describeMismatchOfUnaccapted(Object value, Description description) {
            if (super.acceptValue(value)) {
                ConvertingAdapter.this.describeMismatchOfUnaccapted((Value) value, description);
            } else {
                super.describeMismatchOfUnaccapted(value, description);
            }
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
        protected <I extends Element<V>> ElementMatcher.Result<I> matchResultSafely(I element, ElementMatcher<V> adaptedMatcher, ElementMatcher<? super Property> matcher) {
            final ElementMatcher.Result<?> mr = matcher.matchResult(cachedItem(element));
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
