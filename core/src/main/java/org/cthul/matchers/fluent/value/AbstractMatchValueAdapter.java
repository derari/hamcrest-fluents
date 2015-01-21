package org.cthul.matchers.fluent.value;

import java.util.HashMap;
import java.util.Map;
import org.cthul.matchers.diagnose.QuickDiagnose;
import org.cthul.matchers.diagnose.result.MatchResult;
import org.cthul.matchers.fluent.adapters.SimpleAdapter;
import org.cthul.matchers.fluent.adapters.SimpleAnyOfAdapter;
import org.cthul.matchers.fluent.adapters.SimpleEachOfAdapter;
import org.cthul.matchers.fluent.value.ElementMatcher.Element;
import org.cthul.matchers.object.CIs;
import org.cthul.matchers.object.InstanceOf;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.SelfDescribing;
import org.hamcrest.core.IsEqual;

/**
 * Provides a framework for implementing {@link MatchValue}s that
 * are built by an adapter.
 * <p>
 * Subclasses will implement {@link #adapt(MatchValue)} to return
 * an instance of {@link AbstractAdaptedValue}.
 * <p>
 * User will probably rather implement one of {@link SimpleAdapter},
 * {@link SimpleEachOfAdapter}, or {@link SimpleAnyOfAdapter}.
 * @param <Value>
 * @param <Property>
 * @see SimpleAdapter
 * @see SimpleEachOfAdapter
 * @see SimpleAnyOfAdapter
 */
public abstract class AbstractMatchValueAdapter<Value, Property> extends MatchValueAdapterBase<Value, Property> {
    
    @Override
    public abstract MatchValue<Property> adapt(MatchValue<? extends Value> value);
    
    /**
     * Describes the values this adapter produces from a source {@link MatchValue}.
     * @param source
     * @param description
     * @see #describeProducer(org.hamcrest.SelfDescribing, org.hamcrest.Description) 
     */
    protected void describeValue(MatchValue<? extends Value> source, Description description) {
        describeProducer(source.getValueDescription(), description);
    }
    
    /**
     * Describes the type of values this adapter produces from a source {@link MatchValue}.
     * @param source
     * @param description
     * @see #describeProducer(org.hamcrest.SelfDescribing, org.hamcrest.Description) 
     */
    protected void describeValueType(final MatchValue<? extends Value> source, Description description) {
        describeProducer(source.getValueTypeDescription(), description);
    }
    
    /**
     * Adapts elements of a source {@link MatchValue} and applies matchers on them.
     * <p>
     * Calls to {@link #matched()} and {@link #matchResult()} are directly 
     * delegated to the source value. 
     * For calls to {@link #matches(org.cthul.matchers.fluent.value.ElementMatcher)},
     * the source value will be called with an {@linkplain #adapt(org.cthul.matchers.fluent.value.ElementMatcher) adapted matcher}
     * that will pass all elements to this 
     * <p>  
     * First, it rejects all elements whose values are not of an expected type.
     * Then, {@link #matchSafely(org.cthul.matchers.fluent.value.ElementMatcher.Element, org.cthul.matchers.fluent.value.ElementMatcher) #matchSafely()} 
     * and {@link #matchResultSafely(org.cthul.matchers.fluent.value.ElementMatcher.Element, org.cthul.matchers.fluent.value.ElementMatcher) #matchResultSafely()} 
     * do the actual adapting and apply element matchers respectively.
     * <p>
     * A {@linkplain #cachedItem(org.cthul.matchers.fluent.value.ElementMatcher.Element)  caching mechanism} allows to store state
     * for source values independently.
     * @param <Value>
     * @param <Property> 
     */
    protected abstract static class AbstractAdaptedValue<Value, Property> extends MatchValueBase<Property> {
        
        private final Class<?> valueType;
        private final Matcher<? super Value> precondition;
        private final MatchValue<Value> sourceValue;
        
        private Map<Element<Value>, Object> cache = null;
        private Element<Value> firstKey = null;
        private Object firstCached = null;

        public AbstractAdaptedValue(Class<?> valueType, MatchValue<Value> sourceValue) {
            this.valueType = valueType;
            this.precondition = null;
            this.sourceValue = sourceValue;
        }

        public AbstractAdaptedValue(Matcher<? super Value> precondition, MatchValue<Value> sourceValue) {
            this.valueType = null;
            this.precondition = precondition;
            this.sourceValue = sourceValue;
        }

        public AbstractAdaptedValue(Class<?> valueType, Matcher<Object> precondition, MatchValue<Value> sourceValue) {
            this.valueType = valueType;
            this.precondition = precondition;
            this.sourceValue = sourceValue;
        }
        
        protected boolean acceptValue(Element<?> element) {
            return acceptValue(element.value());
        }
        
        protected boolean acceptValue(Object value) {
            if (valueType != null && !valueType.isInstance(value)) {
                return false;
            }
            if (precondition != null && !precondition.matches(value)) {
                return false;
            }
            return true;
        }
        
        protected MatchResult<?> matchResultOfUnaccepted(Object value) {
            if (valueType != null && !valueType.isInstance(value)) {
                return InstanceOf.isA(valueType).matchResult(value);
            }
            if (precondition != null && !precondition.matches(value)) {
                return QuickDiagnose.matchResult(precondition, value);
            }
            return CIs.isNot(IsEqual.equalTo(value)).matchResult(value);
        }
        
        protected ElementMatcher.Result matchResultOfUnaccepted(Element<?> element) {
            return ElementMatcherWrapper.asElementResult(matchResultOfUnaccepted(element.value()));
        }

        protected MatchValue<Value> getSourceValue() {
            return sourceValue;
        }
        
        protected ElementMatcher<Value> adapt(ElementMatcher<? super Property> matcher) {
            return new InternalAdaptingMatcher<>(this, matcher);
        }

        @Override
        public boolean matched() {
            return sourceValue.matched();
        }
        
        @Override
        public boolean matches(ElementMatcher<? super Property> matcher) {
            // will call #matches(Element, ElementMatcher)
            return sourceValue.matches(adapt(matcher));
        }

        @Override
        public MatchResult<?> matchResult() {
            // will call #matchResult(Element, ElementMatcher)
            return sourceValue.matchResult();
        }
        
        protected abstract void describeConsumer(SelfDescribing sd, Description d);
        
        protected abstract void describeProducer(SelfDescribing sd, Description d);

        protected boolean matches(Element<?> element, ElementMatcher<? super Property> matcher) {
            if (acceptValue(element.value())) {
                return matchSafely((Element) element, matcher);
            } else {
                return false;
            }
        }
        
        protected abstract boolean matchSafely(Element<Value> element, ElementMatcher<? super Property> matcher);
        
        protected ElementMatcher.Result matchResult(final Element<?> element, ElementMatcher<? super Property> matcher) {
            if (acceptValue(element)) {
                return matchResultSafely((Element) element, matcher);
            } else {
                return matchResultOfUnaccepted(element);
            }
        }
        
        /**
         * Returns a {@code MatchResult} that describes the {@code element}'s match.
         * <p>
         * The match result should have {@code element} as value and 
         * {@code adaptedMatcher} as matcher.
         * @param element
         * @param matcher
         * @return result
         */
        protected abstract ElementMatcher.Result matchResultSafely(Element<Value> element, ElementMatcher<? super Property> matcher);
        
        /**
         * Allows to cache state for an element. 
         * If a key is seen for the first time, 
         * {@link #createItem(org.cthul.matchers.fluent.value.ElementMatcher.Element) #createItem()} is called.
         * 
         * @param <T>
         * @param key
         * @return cached value
         */
        protected <T> T cachedItem(Element<Value> key) {
            // before creating a map to manage the cache,
            // we use firstKey and firstCached for the likely case 
            // that only one key will ever occur
            Object value;
            if (cache == null) {
                if (key.equals(firstKey)) {
                    return cast(firstCached);
                }
            } else {
                value = cache.get(key);
                if (value != null) return cast(value);
            }
            value = createItem(key);
            if (cache == null) {
                if (firstKey == null) {
                    firstKey = key;
                    firstCached = value;
                    return cast(value);
                }
                cache = new HashMap<>();
                cache.put(firstKey, firstCached);
            }
            cache.put(key, value);
            return cast(value);
        }
        
        private <T> T cast(Object o) {
            return (T) o;
        }
        
        protected Object createItem(Element<Value> key) {
            throw new UnsupportedOperationException("#createItem not implemented");
        }
    }
    
    /**
     * An {@link ElementMatcher} that delegates all calls to its {@link AbstractAdaptedValue}.
     * @param <Value>
     * @param <Property> 
     */
    protected static class InternalAdaptingMatcher<Value, Property> 
                    implements ElementMatcher<Value> {

        private final AbstractAdaptedValue<Value, Property> adaptedValue;
        private final ElementMatcher<? super Property> itemMatcher;

        public InternalAdaptingMatcher(AbstractAdaptedValue<Value, Property> adaptedValue, ElementMatcher<? super Property> itemMatcher) {
            this.adaptedValue = adaptedValue;
            this.itemMatcher = itemMatcher;
        }

        @Override
        public boolean matches(Element<?> element) {
            return adaptedValue.matches(element, itemMatcher);
        }

        @Override
        public ElementMatcher.Result matchResult(Element<?> e) {
            return adaptedValue.matchResult(e, itemMatcher);
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("[InternalAdaptedMatcher: ");
            description.appendDescriptionOf(itemMatcher);
            description.appendText("]");
        }

        @Override
        public int getDescriptionPrecedence() {
            return P_UNARY_NO_PAREN;
        }
    }
}
