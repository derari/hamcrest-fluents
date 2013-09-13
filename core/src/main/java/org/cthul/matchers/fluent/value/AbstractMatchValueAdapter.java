package org.cthul.matchers.fluent.value;

import java.util.HashMap;
import java.util.Map;
import org.cthul.matchers.diagnose.QuickResultMatcherBase;
import org.cthul.matchers.diagnose.SelfDescribingBase;
import org.cthul.matchers.diagnose.result.AbstractMatchResult;
import org.cthul.matchers.diagnose.result.MatchResult;
import org.cthul.matchers.diagnose.result.MatchResultMismatch;
import org.cthul.matchers.diagnose.result.MatchResultSuccess;
import org.cthul.matchers.fluent.value.ElementMatcher.Element;
import org.cthul.matchers.fluent.value.ElementMatcher.ExpectationDescription;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.SelfDescribing;

/**
 *
 */
public abstract class AbstractMatchValueAdapter<Value, Property> extends MatchValueAdapterBase<Value, Property> {

    @Override
    public MatchValue<Property> adapt(Object value) {
        return adapt(new SingleElementValue<Value>(value));
    }
    
    @Override
    public abstract MatchValue<Property> adapt(MatchValue<? extends Value> value);
    
    protected void describeValue(MatchValue<? extends Value> actual, Description description) {
        describeProducer(actual, description);
    }
    
    protected void describeValueType(final MatchValue<? extends Value> actual, Description description) {
        SelfDescribing actualDescriptor = new SelfDescribingBase() {
            @Override
            public void describeTo(Description description) {
                actual.describeValueType(description);
            }
        };
        describeProducer(actualDescriptor, description);
    }
       
    protected abstract static class AbstractAdaptedValue<Value, Property> extends MatchValueBase<Property> {
        
        private final Class<?> valueType;
        private final MatchValue<Value> actualValue;
        
        private Map<Element<Value>, Object> cache = null;
        private Element<Value> firstKey = null;
        private Object firstCached = null;

        public AbstractAdaptedValue(Class<?> valueType, MatchValue<Value> actualValue) {
            this.valueType = valueType;
            this.actualValue = actualValue;
        }
        
        protected boolean acceptValue(Object value) {
            return valueType.isInstance(value);
        }
        
        protected void describeExpectedToAccept(Object value, Description description) {
            description.appendText("an instance of ")
                       .appendText(valueType.getCanonicalName());
        }
        
        protected void describeMismatchOfUnaccapted(Object value, Description description) {
            description.appendText("was ");
            String s = String.valueOf(value);
            String n = valueType.getSimpleName();
            if (n != null && !s.contains(n)) {
                description.appendText("(")
                        .appendText(n)
                        .appendText(") ");
            }
            description.appendValue(value);
        }
        
        protected <I> ElementMatcher.Mismatch<I> matchResultOfUnaccepted(I element, final Object value, Matcher<?> m) {
            return new MismatchBase<I>(element, m) {
                @Override
                public void describeExpected(ExpectationDescription description) {
                    SelfDescribing sd = new SelfDescribingBase() {
                        @Override
                        public void describeTo(Description description) {
                            describeExpectedToAccept(value, description);
                        }
                    };
                    description.addExpected(-1, sd);
                }
                @Override
                public void describeMismatch(Description description) {
                    describeMismatchOfUnaccapted(value, description);
                }
            };
        }

        protected MatchValue<Value> getActualValue() {
            return actualValue;
        }
        
        protected ElementMatcher<Value> adapt(ElementMatcher<? super Property> matcher) {
            return new InternalAdaptingMatcher<>(this, matcher);
        }

        @Override
        public boolean matched() {
            return actualValue.matched();
        }
        
        @Override
        public boolean matches(ElementMatcher<? super Property> matcher) {
            // will call #matches(Element, ElementMatcher)
            return actualValue.matches(adapt(matcher));
        }

        @Override
        public MatchResult<?> matchResult() {
            // will call #matchResult(Element, ElementMatcher, ElementMatcher)
            return actualValue.matchResult();
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
                return matchResultSafely((Element) element, adaptedMatcher, matcher);
            } else {
                return matchResultOfUnaccepted(element, element.value(), adaptedMatcher);
            }
        }
        
        protected <I extends Element<Value>> ElementMatcher.Result<I> matchResultSafely(I element, ElementMatcher<Value> adaptedMatcher, ElementMatcher<? super Property> matcher) {
            throw new UnsupportedOperationException("matchResultSafely");
        }
        
        protected <T> T cachedItem(Element<Value> key) {
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
                    implements ElementMatcher.Mismatch<Value> {

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
//            return new AbstractMatchResult<I, Matcher<?>>(item, this, matches(item)) {
//                @Override
//                public void describeMatch(Description d) {
//                    adaptedValue.describeMatch(e, itemMatcher, d);
//                }
//                @Override
//                public void describeExpected(Description d) {
//                    ExpectationDescription ex = (ExpectationDescription) d;
//                    adaptedValue.describeExpected(e, itemMatcher, ex);
//                }
//                @Override
//                public void describeMismatch(Description d) {
//                    adaptedValue.describeMismatch(e, itemMatcher, d);
//                }
//            };
        }
        @Override
        public void describeMismatch(Object item, Description description) {
            throw new UnsupportedOperationException("internal use only");
        }
    }    
}
