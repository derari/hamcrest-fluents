package org.cthul.matchers.fluent.value;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.cthul.matchers.fluent.value.MatchValue.Element;
import org.cthul.matchers.fluent.value.MatchValue.ElementMatcher;
import org.cthul.matchers.fluent.value.MatchValue.ExpectationDescription;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 *
 */
public abstract class AbstractMatchValueAdapter<Value, Item> implements MatchValueAdapter<Value, Item> {

    @Override
    public MatchValue<Item> adapt(Value v) {
        return adapt(new SingleElementValue<>(v));
    }
    
    @Override
    public abstract MatchValue<Item> adapt(MatchValue<Value> v);

    @Override
    public <Value0> MatchValueAdapter<Value0, Item> adapt(MatchValueAdapter<Value0, Value> adapter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    protected static class AdaptedMatchValueAdapter<Value, Item, Item2> extends AbstractMatchValueAdapter<Value, Item2> {
        
        private final MatchValueAdapter<Value, Item> mva1;
        private final MatchValueAdapter<Item, Item2> mva2;

        public AdaptedMatchValueAdapter(MatchValueAdapter<Value, Item> mva1, MatchValueAdapter<Item, Item2> mva2) {
            this.mva1 = mva1;
            this.mva2 = mva2;
        }

        @Override
        public MatchValue<Item2> adapt(MatchValue<Value> v) {
            return mva2.adapt(mva1.adapt(v));
        }

        @Override
        public void describeMatcher(Matcher<? super Item2> matcher, Description description) {
            mva1.describeMatcher(new AdaptingMatcher<>(mva2, matcher), description);
        }
        
    }
    
    protected abstract static class AbstractAdaptedValue<Value, Item> extends AbstractMatchValue<Item> {
        
        private final MatchValue<Value> actualValue;
        
        private Map<Element<Value>, Object> cache;
        private Element<Value> firstKey;
        private Object firstCached;

        public AbstractAdaptedValue(MatchValue<Value> actualValue) {
            this.actualValue = actualValue;
        }

        protected MatchValue<Value> getActualValue() {
            return actualValue;
        }
        
        protected ElementMatcher<Value> adapt(ElementMatcher<Item> matcher) {
            return new InternalAdaptedMatcher<>(this, matcher);
        }
        
        @Override
        public boolean matches(ElementMatcher<Item> matcher) {
            return actualValue.matches(adapt(matcher));
        }

        @Override
        public void describeExpected(ExpectationDescription description) {
            actualValue.describeExpected(description);
        }

        @Override
        public void describeMismatch(Description description) {
            actualValue.describeMismatch(description);
        }
        
        @Override
        public boolean matched() {
            return actualValue.matched();
        }
        
        protected abstract boolean matches(Element<Value> element, ElementMatcher<Item> matcher);
        
        protected abstract void describeExpected(Element<Value> element, ElementMatcher<Item> matcher, ExpectationDescription description);
        
        protected abstract void describeMismatch(Element<Value> element, ElementMatcher<Item> matcher, Description description);
        
        private <T> T cast(Object o) {
            return (T) o;
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
        
        protected Object createItem(Element<Value> key) {
            throw new UnsupportedOperationException("#createItem not implemented");
        }
        
    }
    
    protected static class InternalAdaptedMatcher<Value, Item> 
                    extends BaseMatcher<Element<Value>> 
                    implements ElementMatcher<Value> {

        private final AbstractAdaptedValue<Value, Item> adaptedValue;
        private final ElementMatcher<Item> itemMatcher;

        public InternalAdaptedMatcher(AbstractAdaptedValue<Value, Item> adaptedValue, ElementMatcher<Item> itemMatcher) {
            this.adaptedValue = adaptedValue;
            this.itemMatcher = itemMatcher;
        }

        @Override
        public boolean matches(Object item) {
            Element<Value> e = (Element) item;
            return adaptedValue.matches(e, itemMatcher);
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("[InternalAdaptedMatcher: ");
            description.appendDescriptionOf(itemMatcher);
            description.appendText("]");
        }

        @Override
        public void describeExpected(Element<Value> e, ExpectationDescription description) {
            adaptedValue.describeExpected(e, itemMatcher, description);
        }
        
        @Override
        public void describeMismatch(Object item, Description description) {
            Element<Value> e = (Element) item;
            adaptedValue.describeMismatch(e, itemMatcher, description);
        }
    }
    
    protected static class SingleElementValue<Value> 
                    extends AbstractMatchValue<Value>
                    implements Element<Value> {

        private final Value value;
        private ElementMatcher<Value> mismatch = null;
        private String valueType = null;

        public SingleElementValue(Value value) {
            this.value = value;
        }

        @Override
        public boolean matches(ElementMatcher<Value> matcher) {
            if (mismatch != null) return false;
            if (matcher.matches(this)) {
                return true;
            }
            mismatch = matcher;
            return false;
        }

        @Override
        public boolean matched() {
            return mismatch == null;
        }

        @Override
        public void describeTo(Description description) {
            description.appendValue(value);
        }

        @Override
        public void describeValueType(Description description) {
            if (valueType == null) {
                valueType = getValueType();
            }
            description.appendText(valueType);
        }
        
        protected String getValueType() {
            if (value != null) {
                if (value instanceof Iterable) {
                    if (value instanceof List) {
                        return "list";
                    } else if (value instanceof Set) {
                        return "set";
                    } else if (value instanceof Collection) {
                        return "collection";
                    }
                }
                if (value instanceof Map) {
                    return "map";
                } else if (value.getClass().isArray()) {
                    return "array";
                }
            }
           return "value";
        }

        @Override
        public void describeExpected(ExpectationDescription description) {
            mismatch.describeExpected(this, description);
        }

        @Override
        public void describeMismatch(Description description) {
            mismatch.describeMismatch(this, description);
        }

        @Override
        public Value value() {
            return value;
        }
    }
    
}
