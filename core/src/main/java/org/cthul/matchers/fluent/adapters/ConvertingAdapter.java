package org.cthul.matchers.fluent.adapters;

import org.cthul.matchers.fluent.value.AbstractMatchValueAdapter;
import org.cthul.matchers.fluent.value.MatchValue;
import org.cthul.matchers.fluent.value.MatchValue.Element;
import org.cthul.matchers.fluent.value.MatchValue.ElementMatcher;
import org.cthul.matchers.fluent.value.MatchValue.ExpectationDescription;
import org.hamcrest.Description;
import org.hamcrest.internal.ReflectiveTypeFinder;

/**
 * 
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
    public MatchValue<Property> adapt(MatchValue<Value> v) {
        return new ConvertedMatchValue(valueType, v);
    }
    
    protected abstract Property adaptValue(Value v);
    
    protected class ConvertedMatchValue extends AbstractAdaptedValue<Value, Property> {

        public ConvertedMatchValue(Class<?> valueType, MatchValue<Value> actualValue) {
            super(valueType, actualValue);
        }

        @Override
        protected Converted<Property> createItem(Element<Value> key) {
            Property i = adaptValue(key.value());
            return new Converted<>(i);
        }

        @Override
        protected boolean matchSafely(Element<Value> element, ElementMatcher<Property> matcher) {
            return matcher.matches(cachedItem(element));
        }

        @Override
        public void describeTo(Description description) {
            ConvertingAdapter.this.describeValue(getActualValue(), description);
        }

        @Override
        public void describeValueType(Description description) {
            ConvertingAdapter.this.describeValueType(getActualValue(), description);
        }

        @Override
        protected void describeExpectedSafely(Element<Value> element, ElementMatcher<Property> matcher, ExpectationDescription description) {
            Element<Property> item = cachedItem(element);
            ConvertingAdapter.this.describeExpected(element, item, matcher, description);
        }

        @Override
        protected void describeMismatchSafely(Element<Value> element, ElementMatcher<Property> matcher, Description description) {
            Element<Property> item = cachedItem(element);
            ConvertingAdapter.this.describeMismatch(element, item, matcher, description);
        }
    }
    
    protected static class Converted<Item> implements Element<Item> {
        
        private final Item item;

        public Converted(Item item) {
            this.item = item;
        }

        @Override
        public Item value() {
            return item;
        }
    }
}
