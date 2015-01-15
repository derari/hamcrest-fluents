package org.cthul.matchers.fluent.value;

import java.util.HashMap;
import java.util.Map;
import org.cthul.matchers.diagnose.QuickDiagnose;
import org.cthul.matchers.diagnose.QuickResultMatcherBase;
import org.cthul.matchers.diagnose.result.AbstractMatchResult;
import org.cthul.matchers.diagnose.result.MatchResult;
import org.cthul.matchers.diagnose.result.MatchResultMismatch;
import org.cthul.matchers.diagnose.result.MatchResultProxy;
import org.cthul.matchers.diagnose.result.MatchResultSuccess;
import org.cthul.matchers.fluent.adapters.SimpleAdapter;
import org.cthul.matchers.fluent.adapters.SimpleAnyOfAdapter;
import org.cthul.matchers.fluent.adapters.SimpleEachOfAdapter;
import org.cthul.matchers.fluent.value.ElementMatcher.Element;
import org.cthul.matchers.fluent.value.ElementMatcher.ExpectationDescription;
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
     * and {@link #matchResultSafely(org.cthul.matchers.fluent.value.ElementMatcher.Element, org.cthul.matchers.fluent.value.ElementMatcher, org.cthul.matchers.fluent.value.ElementMatcher) #matchResultSafely()} 
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
        
//        protected void describeExpectedToAccept(Object value, Description description) {
//            description.appendText("is an instance of ")
//                       .appendText(valueType.getCanonicalName());
//        }
//        
//        protected void describeMismatchOfUnaccapted(Object value, Description description) {
//            description.appendText("was ");
//            String s = String.valueOf(value);
//            String n = valueType.getSimpleName();
//            if (n != null && !s.contains(n)) {
//                description.appendText("(")
//                        .appendText(n)
//                        .appendText(") ");
//            }
//            description.appendValue(value);
//        }
        
        protected <I> ElementMatcher.Mismatch<I> matchResultOfUnaccepted(I element, final Object value, Matcher<?> m) {
            return new ResultProxy<>(matchResultOfUnaccepted(value), element, m);
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
            // will call #matchResult(Element, ElementMatcher, ElementMatcher)
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
        
        protected <I extends Element<?>> ElementMatcher.Result<I> matchResult(final I element, ElementMatcher<Value> adaptedMatcher, ElementMatcher<? super Property> matcher) {
            if (acceptValue(element.value())) {
                return (ElementMatcher.Result) matchResultSafely((Element) element, adaptedMatcher, matcher);
            } else {
                return AbstractAdaptedValue.this.matchResultOfUnaccepted(element, element.value(), adaptedMatcher);
            }
        }
        
        /**
         * Returns a {@code MatchResult} that describes the {@code element}'s match.
         * <p>
         * The match result should have {@code element} as value and 
         * {@code adaptedMatcher} as matcher. It is recommended to extend
         * {@link MatchBase}, {@link MismatchBase}, or {@link ResultBase}.
         * @param <I>
         * @param element
         * @param adaptedMatcher
         * @param matcher
         * @return 
         */
        protected abstract <I extends Element<Value>> ElementMatcher.Result<I> matchResultSafely(I element, ElementMatcher<Value> adaptedMatcher, ElementMatcher<? super Property> matcher);
        
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
    
    protected abstract static class MismatchBase<Value> 
                    extends MatchResultMismatch<Value, Matcher<?>>
                    implements ElementMatcher.Result<Value>, ElementMatcher.Mismatch<Value> {

        public MismatchBase(Value value, Matcher<?> matcher) {
            super(value, matcher);
        }

        @Override
        public ElementMatcher.Mismatch<Value> getMismatch() {
            return this;
        }

        @Override
        public final void describeExpected(Description d) {
            throw new UnsupportedOperationException("internal use only");
        }
    }
    
    protected abstract static class MatchBase<Value> 
                    extends MatchResultSuccess<Value, Matcher<?>>
                    implements ElementMatcher.Result<Value> {

        public MatchBase(Value value, Matcher<?> matcher) {
            super(value, matcher);
        }

        @Override
        public ElementMatcher.Mismatch<Value> getMismatch() {
            return null;
        }
    }
    
    protected abstract static class ResultBase<Value> 
                    extends AbstractMatchResult<Value, Matcher<?>>
                    implements ElementMatcher.Result<Value>, ElementMatcher.Mismatch<Value> {

        /**
         * When this constructor is used, 
         * {@link #matched()} should be overridden.
         * @param value
         * @param matcher 
         */
        public ResultBase(Value value, Matcher<?> matcher) {
            super(value, matcher);
        }

        public ResultBase(Value value, Matcher<?> matcher, boolean success) {
            super(value, matcher, success);
        }

        @Override
        public ElementMatcher.Mismatch<Value> getMismatch() {
            return (ElementMatcher.Mismatch<Value>) super.getMismatch();
        }

        @Override
        public final void describeExpected(Description d) {
            throw new UnsupportedOperationException("internal use only");
        }
    }
    
    protected static class ResultProxy<Value>
                    extends MatchResultProxy<Value, Matcher<?>>
                    implements ElementMatcher.Result<Value>, ElementMatcher.Mismatch<Value> {

        public ResultProxy(MatchResult<?> result, Value value, Matcher<?> matcher) {
            super(result, value, matcher);
        }

        @Override
        public ElementMatcher.Mismatch<Value> getMismatch() {
            return (ElementMatcher.Mismatch<Value>) super.getMismatch();
        }

        @Override
        public void describeExpected(ExpectationDescription description) {
            description.addExpected(-1, getExpectedDescription());
        }
    }
    /**
     * An {@link ElementMatcher} that delegates all calls to its {@link AbstractAdaptedValue}.
     * @param <Value>
     * @param <Property> 
     */
    protected static class InternalAdaptingMatcher<Value, Property> 
                    extends QuickResultMatcherBase<Element<?>> 
                    implements ElementMatcher<Value> {

        private final AbstractAdaptedValue<Value, Property> adaptedValue;
        private final ElementMatcher<? super Property> itemMatcher;

        public InternalAdaptingMatcher(AbstractAdaptedValue<Value, Property> adaptedValue, ElementMatcher<? super Property> itemMatcher) {
            this.adaptedValue = adaptedValue;
            this.itemMatcher = itemMatcher;
        }

        @Override
        public boolean matches(Object item) {
            Element<?> e = (Element) item;
            return adaptedValue.matches(e, itemMatcher);
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("[InternalAdaptedMatcher: ");
            description.appendDescriptionOf(itemMatcher);
            description.appendText("]");
        }

        @Override
        public <I> ElementMatcher.Result<I> matchResult(I item) {
            final Element<?> e = (Element) item;
            return (ElementMatcher.Result) adaptedValue.matchResult(e, this, itemMatcher);
        }
        
        /**
         * Use {@link #matchResult(java.lang.Object)} instead.
         * <p>
         * This matcher should never occur in a context 
         * where this method needs to be called.
         * @param description
         */
        @Override
        public void describeMismatch(Object item, Description description) {
            throw new UnsupportedOperationException("matcher for internal use only");
        }
    }
}
