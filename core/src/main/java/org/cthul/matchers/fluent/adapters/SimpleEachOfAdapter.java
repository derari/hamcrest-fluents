package org.cthul.matchers.fluent.adapters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.cthul.matchers.diagnose.nested.Nested;
import org.cthul.matchers.fluent.value.AbstractMatchValueAdapter;
import org.cthul.matchers.fluent.value.MatchValue;
import org.cthul.matchers.fluent.value.ElementMatcher;
import org.cthul.matchers.fluent.value.ElementMatcher.Element;
import org.cthul.matchers.fluent.value.ElementMatcher.ExpectationDescription;
import org.cthul.matchers.fluent.value.ElementMatcher.Result;
import org.hamcrest.Description;
import org.hamcrest.SelfDescribing;
import org.hamcrest.internal.ReflectiveTypeFinder;

/**
 * Base class for adapters that return multiple items from a source value,
 * and require all to be matched.
 * @param <Value>
 * @param <Item>
 */
public abstract class SimpleEachOfAdapter<Value, Item> 
                extends AbstractMatchValueAdapter<Value, Item> {

    protected static final ReflectiveTypeFinder ADAPTED_TYPE_FINDER = new ReflectiveTypeFinder("getElements", 1, 0);
    
    private final Class<?> valueType;
    private final String name;

    public SimpleEachOfAdapter() {
        this(null, ADAPTED_TYPE_FINDER);
    }

    public SimpleEachOfAdapter(String name) {
        this(name, ADAPTED_TYPE_FINDER);
    }

    public SimpleEachOfAdapter(Class<?> valueType) {
        this(null, valueType);
    }
    
    public SimpleEachOfAdapter(ReflectiveTypeFinder typeFinder) {
        this(null, typeFinder);
    }
    
    public SimpleEachOfAdapter(String name, Class<?> valueType) {
        this.name = name;
        this.valueType = valueType;
    }
    
    public SimpleEachOfAdapter(String name, ReflectiveTypeFinder typeFinder) {
        this.name = name;
        this.valueType = typeFinder.findExpectedType(getClass());
    }
    
    @Override
    public MatchValue<Item> adapt(MatchValue<? extends Value> value) {
        return new EachOfValues<>(valueType, value);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("each");
        if (name != null) {
            description.appendText(" ").appendText(name);
        }
    }

    @Override
    protected void describeResult(SelfDescribing result, Description description) {
        result.describeTo(description);
    }
    
    protected abstract Iterable<? extends Item> getElements(Value value);
    
    protected void describeElement(Value owner, Item element, int index, Description description) {
        if (name != null) {
            description.appendText(name).appendText(" ");
        } else {
            description.appendText("#");
        }
        description.appendText(String.valueOf(index))
                   .appendText(" ");
    }

    protected class EachOfValues<V extends Value> extends AbstractAdaptedValue<V, Item> {

        public EachOfValues(Class<?> valueType, MatchValue<V> actualValue) {
            super(valueType, actualValue);
        }

        @Override
        protected Object createItem(Element<V> key) {
            return new SourceElements<>(getElements(key.value()));
        }

        @Override
        protected boolean matchSafely(Element<V> element, ElementMatcher<? super Item> matcher) {
            SourceElements<Item> it = cachedItem(element);
            return findInvalidItem(it, matcher) == null;
        }
        
        protected E<Item> findInvalidItem(SourceElements<Item> elements, ElementMatcher<? super Item> matcher) {
            if (elements.invalid != null) return elements.invalid;
            for (E<Item> e: elements) {
                if (!matcher.matches(e)) {
                    e.mismatch = matcher;
                    elements.invalid = e;
                    return e;
                }
            }
            return null;
        }

        @Override
        protected Result matchResultSafely(Element<V> element, ElementMatcher<? super Item> matcher) {
            SourceElements<Item> it = cachedItem(element);
            E<Item> invalid = findInvalidItem(it, matcher);
            if (invalid == null) {
                return successResult(element, it, matcher);
            } else {
                return failResult(element, invalid);
            }
        }
        
        protected Result successResult(final Element<V> owner, final SourceElements<Item> it, final ElementMatcher<? super Item> matcher) {
            return new InternalResult(true) {
                List<Result> matches = null;
                @Override
                public void describeTo(Description description) {
                    if (matches == null) {
                        matches = new ArrayList<>();
                        for (E<Item> e: it) {
                            matches.add(matcher.matchResult(e));
                        }
                    }
                    for (E<Item> e: Nested.collectDescriptions(it, description, ", ", ", and ", " and ")) {
                        describeElement(owner.value(), e.value(), e.i, description);
                        matches.get(e.i).describeTo(description);
                    }
                }
                @Override
                public void describeExpected(ExpectationDescription description) {
                    for (E<Item> e: it) {
                        e.mismatchResult().describeExpected(description);
                    }
                }
            };
        }
        
        protected Result failResult(final Element<V> owner, final E<Item> item) {
            return new InternalResult(false) {
                @Override
                public void describeTo(Description description) {
                    describeElement(owner.value(), item.value(), item.i, description);
                    item.mismatchResult().describeTo(description);
                }
                @Override
                public void describeExpected(ExpectationDescription description) {
                    item.mismatchResult().describeExpected(description);
                }
            };
        }

        @Override
        public void describeValue(Description description) {
            SimpleEachOfAdapter.this.describeValue(getSourceValue(), description);
        }

        @Override
        public void describeValueType(Description description) {
            SimpleEachOfAdapter.this.describeValueType(getSourceValue(), description);
        }

        @Override
        protected void describeProducer(SelfDescribing sd, Description d) {
            SimpleEachOfAdapter.this.describeProducer(sd, d);
        }

        @Override
        protected void describeConsumer(SelfDescribing sd, Description d) {
            SimpleEachOfAdapter.this.describeConsumer(sd, d);
        }
    }
    
    protected static class SourceElements<Item> implements Iterable<E<Item>> {
        
        private final Iterator<? extends Item> source;
        private final List<E<Item>> list = new ArrayList<>();
        E<Item> invalid = null;
        
        public SourceElements(Iterable<? extends Item> iterable) {
            this.source = iterable.iterator();
        }
        
        @Override
        public Iterator<E<Item>> iterator() {
            return new Iterator<E<Item>>() {
                Iterator<E<Item>> previous = list.iterator();
                @Override
                public boolean hasNext() {
                    return (previous != null && previous.hasNext()) || source.hasNext();
                }

                @Override
                public E<Item> next() {
                    if (previous != null && previous.hasNext()) {
                        return previous.next();
                    }
                    previous = null;
                    E<Item> next = new E<>(source.next(), list.size());
                    list.add(next);
                    return next;
                }
            };
        }
    }
    
    protected static class E<Item> implements Element<Item> {
        
        private final Item value;
        final int i;
        ElementMatcher<? super Item> mismatch = null;
        Result mismatchResult = null;
        E<Item> next = null;

        public E(Item value, int i) {
            this.value = value;
            this.i = i;
        }

        @Override
        public Item value() {
            return value;
        }

        public Result mismatchResult() {
            if (mismatchResult == null) {
                mismatchResult = mismatch.matchResult(this);
            }
            return mismatchResult;
        }
    }
}
