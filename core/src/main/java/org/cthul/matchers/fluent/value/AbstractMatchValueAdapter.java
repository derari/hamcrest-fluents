package org.cthul.matchers.fluent.value;

import java.util.HashMap;
import java.util.Map;
import org.cthul.matchers.diagnose.QuickResultMatcherBase;
import org.cthul.matchers.diagnose.result.AbstractMatchResult;
import org.cthul.matchers.diagnose.result.MatchResult;
import org.cthul.matchers.diagnose.result.MatchResultMismatch;
import org.cthul.matchers.fluent.value.MatchValue.Element;
import org.cthul.matchers.fluent.value.MatchValue.ElementMatcher;
import org.cthul.matchers.fluent.value.MatchValue.ExpectationDescription;
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
    
    protected void describeExpected(Element<? extends Value> value, final Element<Property> item, final ElementMatcher<Property> matcher, ExpectationDescription description) {
        SelfDescribing expectedDescriptor = new SelfDescribingBase() {
            @Override
            public void describeTo(Description description) {
                ExpectationDescription ed = (ExpectationDescription) description;
                matcher.describeExpected(item, ed);
            }
        };
        describeConsumer(expectedDescriptor, description);
    }
    
    protected void describeMismatch(Element<? extends Value> value, final Element<Property> item, final ElementMatcher<Property> matcher, Description description) {
        SelfDescribing mismatchDescriptor = new SelfDescribingBase() {
            @Override
            public void describeTo(Description description) {
                matcher.describeMismatch(item, description);
            }
        };
        describeConsumer(mismatchDescriptor, description);
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

        protected MatchValue<Value> getActualValue() {
            return actualValue;
        }
        
        protected ElementMatcher<Value> adapt(ElementMatcher<Property> matcher) {
            return new InternalAdaptingMatcher<>(this, matcher);
        }
        
        @Override
        public boolean matches(ElementMatcher<Property> matcher) {
            // will call #matches(Element, ElementMatcher)
            return actualValue.matches(adapt(matcher));
        }

        @Override
        public void describeMatch(Description description) {
            // will call #describeMatch(Element, ElementMatcher, Description)
            actualValue.getMatch().describeMatch(description);
        }

        @Override
        public void describeExpected(ExpectationDescription description) {
            // will call #describeExpected(Element, ElementMatcher, Description)
            actualValue.getMismatch().describeExpected(description);
        }

        @Override
        public void describeMismatch(Description description) {
            // will call #describeMismatch(Element, ElementMatcher, Description)
            actualValue.getMismatch().describeMismatch(description);
        }
        
        @Override
        public boolean matched() {
            return actualValue.matched();
        }
        
        protected boolean matches(Element<?> element, ElementMatcher<Property> matcher) {
            if (acceptValue(element.value())) {
                return matchSafely((Element) element, matcher);
            } else {
                return false;
            }
        }
        
        protected abstract boolean matchSafely(Element<Value> element, ElementMatcher<Property> matcher);
        
        protected <I extends Element<?>> MatchResult<I> matchResult(final I element, ElementMatcher<Value> adaptedMatcher, ElementMatcher<Property> matcher) {
            if (acceptValue(element.value())) {
                return matchResultSafely((Element) element, adaptedMatcher, matcher);
            } else {
                return new MatchResultMismatch<I, Matcher<?>>(element, matcher) {
                    @Override
                    public void describeExpected(Description d) {
                        ExpectationDescription ex = (ExpectationDescription) d;
                        SelfDescribing sd = new SelfDescribingBase() {
                            @Override
                            public void describeTo(Description description) {
                                describeExpectedToAccept(element.value(), description);
                            }
                        };
                        ex.addExpected(-1, sd);
                    }
                };
            }
        }
        
        protected <I extends Element<Value>> MatchResult<I> matchResultSafely(I element, ElementMatcher<Value> adaptedMatcher, ElementMatcher<Property> matcher) {
            throw new UnsupportedOperationException("matchResultSafely");
        }
        
        protected void describeMatch(final Element<?> element, ElementMatcher<Property> matcher, Description description) {
            if (true) throw new UnsupportedOperationException("deprecated");
            describeMatchSafely((Element) element, matcher, description);
        }
        
        protected abstract void describeMatchSafely(Element<Value> element, ElementMatcher<Property> matcher, Description description);
        
        protected void describeExpected(final Element<?> element, ElementMatcher<Property> matcher, ExpectationDescription description) {
            if (true) throw new UnsupportedOperationException("deprecated");
            if (acceptValue(element.value())) {
                describeExpectedSafely((Element) element, matcher, description);
            } else {
                SelfDescribing sd = new SelfDescribingBase() {
                    @Override
                    public void describeTo(Description description) {
                        describeExpectedToAccept(element.value(), description);
                    }
                };
                description.addExpected(-1, sd);
            }
        }
        
        protected abstract void describeExpectedSafely(Element<Value> element, ElementMatcher<Property> matcher, ExpectationDescription description);
        
        protected void describeMismatch(Element<?> element, ElementMatcher<Property> matcher, Description description) {
            if (true) throw new UnsupportedOperationException("deprecated");
            if (acceptValue(element.value())) {
                describeMismatchSafely((Element) element, matcher, description);
            } else {
                describeMismatchOfUnaccapted(element.value(), description);
            }
        }
        
        protected abstract void describeMismatchSafely(Element<Value> element, ElementMatcher<Property> matcher, Description description);
        
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
    
    protected static class InternalAdaptingMatcher<Value, Property> 
                    extends QuickResultMatcherBase<Element<?>> 
                    implements ElementMatcher<Value> {

        private final AbstractAdaptedValue<Value, Property> adaptedValue;
        private final ElementMatcher<Property> itemMatcher;

        public InternalAdaptingMatcher(AbstractAdaptedValue<Value, Property> adaptedValue, ElementMatcher<Property> itemMatcher) {
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
        public <I> MatchResult<I> matchResult(I item) {
            final Element<?> e = (Element) item;
            return (MatchResult) adaptedValue.matchResult(e, this, itemMatcher);
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
        public void describeExpected(Element<?> e, ExpectationDescription description) {
            throw new UnsupportedOperationException("deprecated");
            //adaptedValue.describeExpected(e, itemMatcher, description);
        }
        
        @Override
        public void describeMismatch(Object item, Description description) {
            Element<?> e = (Element) item;
            adaptedValue.describeMismatch(e, itemMatcher, description);
        }
    }    
}
