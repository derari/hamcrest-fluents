package org.cthul.matchers.fluent.adapters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.cthul.matchers.diagnose.SelfDescribingBase;
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
 * but require only one to be matched.
 */
public abstract class SimpleAnyOfAdapter<Value, Item> 
                extends AbstractMatchValueAdapter<Value, Item> {

    protected static final ReflectiveTypeFinder ADAPTED_TYPE_FINDER = new ReflectiveTypeFinder("getElements", 1, 0);
    
    private final Class<?> valueType;
    private final String name;

    public SimpleAnyOfAdapter() {
        this(null, ADAPTED_TYPE_FINDER);
    }

    public SimpleAnyOfAdapter(String name) {
        this(name, ADAPTED_TYPE_FINDER);
    }

    public SimpleAnyOfAdapter(Class<?> valueType) {
        this(null, valueType);
    }
    
    public SimpleAnyOfAdapter(ReflectiveTypeFinder typeFinder) {
        this(null, typeFinder);
    }
    
    public SimpleAnyOfAdapter(String name, Class<?> valueType) {
        this.name = name;
        this.valueType = valueType;
    }
    
    public SimpleAnyOfAdapter(String name, ReflectiveTypeFinder typeFinder) {
        this.name = name;
        this.valueType = typeFinder.findExpectedType(getClass());
    }
    
    @Override
    public MatchValue<Item> adapt(MatchValue<? extends Value> value) {
        return new AnyOfValues<>(valueType, value);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("any");
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
//        // since the any-adapter mismatch prints every item anyway,
//        // print the name only once and don't print indices
//        if (index == 0 && name != null) {
//            description.appendText(name).appendText(" ");
//        }
        
        if (name != null) {
            description.appendText(name).appendText(" ");
        } else {
            description.appendText("#");
        }
        description.appendText(String.valueOf(index))
                   .appendText(" ");
    }

    protected class AnyOfValues<V extends Value> extends AbstractAdaptedValue<V, Item> {

        private PreviousMatcher<Item> matchers = null;
        private PreviousMatcher<Item> matchersEnd = null;
        
        public AnyOfValues(Class<?> valueType, MatchValue<V> actualValue) {
            super(valueType, actualValue);
        }

        @Override
        protected Object createItem(Element<V> key) {
            return new RemainingValidElements<>(getElements(key.value()));
        }

        @Override
        protected boolean matchSafely(Element<V> element, ElementMatcher<? super Item> matcher) {
            RemainingValidElements<Item> it = cachedItem(element);
            return findValidItem(it, matcher) != null;
        }
        
        protected E<Item> findValidItem(RemainingValidElements<Item> remaining, ElementMatcher<? super Item> matcher) {
            PreviousMatcher<Item> pm = addToPreviousChain(matcher);
            E<Item> current = remaining.getCurrent();
            if (current != null) {
                if (matcher.matches(current)) {
                    return current;
                }
                pm.success = false;
                current.mismatch = matcher;
            }
            for (E<Item> e: remaining.more()) {
                if (matchAgainstPrevious(e)) {
                    return e;
                }
            }
            return null;
        }
        
        protected PreviousMatcher<Item> addToPreviousChain(ElementMatcher<? super Item> matcher) {
            for (PreviousMatcher<Item> pm = matchers; pm != null; pm = pm.next) {
                if (pm.matcher == matcher) {
                    return pm;
                }
            }
            PreviousMatcher<Item> pm = new PreviousMatcher<>(matcher);
            if (matchers == null) {
                matchersEnd = matchers = pm;
            } else {
                matchersEnd.next = pm;
                matchersEnd = pm;
            }
            return pm;
        }
        
        protected boolean matchAgainstPrevious(E<Item> e) {
            // first, try only matchers that have already failed
            for (PreviousMatcher<Item> pm = matchers; pm != null; pm = pm.next) {
                if (pm.success) continue;
                if (!pm.matcher.matches(e)) {
                    e.mismatch = pm.matcher;
                    return false;
                }
            }
            // now, try the previously successful matchers, too
            for (PreviousMatcher<Item> pm = matchers; pm != null; pm = pm.next) {
                if (!pm.success) continue;
                if (!pm.matcher.matches(e)) {
                    pm.success = false;
                    e.mismatch = pm.matcher;
                    return false;
                }
            }
            return true;
        }

        @Override
        protected Result matchResultSafely(Element<V> element, ElementMatcher<? super Item> matcher) {
            RemainingValidElements<Item> it = cachedItem(element);
            E<Item> e = findValidItem(it, matcher);
            if (e == null) {
                return failResult(element, it);
            } else {
                return successResult(element, e);
            }
        }
        
        protected Result successResult(final Element<V> owner, final E<Item> e) {
            final List<Result> matches = new ArrayList<>();
            for (PreviousMatcher<Item> pm = matchers; pm != null; pm = pm.next) {
                matches.add(pm.matcher.matchResult(e));
            }
            return new InternalResult(true) {
                @Override
                public void describeTo(Description description) {
                    for (Result r: Nested.collectDescriptions(matches, description, ", ", ", and ", " and "))  {
                        describeElement(owner.value(), e.value(), e.getIndex(), description);
                        r.describeTo(description);
                    }
                }
                @Override
                public void describeExpected(ExpectationDescription description) {
                    for (Result r: matches) {
                        r.describeExpected(description);
                    }
                }
            };
        }
        
        protected Result failResult(final Element<V> owner, final RemainingValidElements<Item> remaining) {            
            return new InternalResult(false) {
                @Override
                public void describeTo(Description description) {
                    List<E<Item>> invalids = remaining.getInvalidElements();
                    for (E<Item> e: Nested.collectDescriptions(invalids, description, ", ", ", and ", " and ")) {
                        describeElement(owner.value(), e.value(), e.getIndex(), description);
                        e.mismatchResult().describeTo(description);
                    }
                }
                @Override
                public void describeExpected(ExpectationDescription description) {
                    List<E<Item>> invalids = remaining.getInvalidElements();
                    for (E<Item> e: invalids) {
                        e.mismatchResult().describeExpected(description);
                    }
                }
            };
        }

        @Override
        public void describeValue(Description description) {
            SimpleAnyOfAdapter.this.describeValue(getSourceValue(), description);
        }

        @Override
        public void describeValueType(Description description) {
            SimpleAnyOfAdapter.this.describeValueType(getSourceValue(), description);
        }

        @Override
        protected void describeProducer(SelfDescribing sd, Description d) {
            SimpleAnyOfAdapter.this.describeProducer(sd, d);
        }

        @Override
        protected void describeConsumer(SelfDescribing sd, Description d) {
            SimpleAnyOfAdapter.this.describeConsumer(sd, d);
        }
        
//        @Override
//        protected void describeMatchSafely(Element<V> element, ElementMatcher<Item> matcher, Description description) {
//            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//        }
//
//        @Override
//        protected void describeExpectedSafely(Element<V> element, ElementMatcher<Item> matcher, ExpectationDescription description) {
//            AnyItemIterable<Item> it = cachedItem(element);
//            for (E<Item> e = it.first(); e != null; e = it.next(e)) {
//                e.mismatch.describeExpected(e, description);
//            }
//        }
//
//        @Override
//        protected void describeMismatchSafely(Element<V> element, ElementMatcher<Item> matcher, Description description) {
//            AnyItemIterable<Item> it = cachedItem(element);
//            E<Item> first = it.first();
//            E<Item> e = first;
//            while (e != null) {
//                if (e != first) {
//                    if (e.next == null) {
//                        description.appendText(e == first.next ? " " : ", ");
//                        description.appendText("and ");
//                    } else {
//                        description.appendText(", ");
//                    }
//                }
//                describeElement(element.value(), e.value(), e.i, description);
//                e.mismatch.describeMismatch(e, description);
//                e = it.next(e);
//            }
//        }

    }
    
    protected static class RemainingValidElements<Item> {
        
        private final Iterator<? extends Item> source;
        private final List<E<Item>> invalidElements = new ArrayList<>();
        private E<Item> current = null;
        
        public RemainingValidElements(Iterable<? extends Item> iterable) {
            this.source = iterable.iterator();
        }

        public E<Item> getCurrent() {
            return current;
        }

        public List<E<Item>> getInvalidElements() {
            return invalidElements;
        }
        
        public Iterable<E<Item>> more() {
            return new Iterable<E<Item>>() {
                @Override
                public Iterator<E<Item>> iterator() {
                    return iterateMore();
                }
            };
        }

        private Iterator<E<Item>> iterateMore() {
            return new Iterator<E<Item>>() {
                @Override
                public boolean hasNext() {
                    if (current != null) {
                        invalidElements.add(current);
                        current = null;
                    }
                    return source.hasNext();
                }
                @Override
                public E<Item> next() {
                    return current = new E<>(source.next(), invalidElements.size());
                }
                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }
    
    protected static class E<Item> implements Element<Item> {
        
        private final Item value;
        private final int index;
        private ElementMatcher<? super Item> mismatch = null;
        private ElementMatcher.Result mismatchResult = null;

        public E(Item value, int i) {
            this.value = value;
            this.index = i;
        }

        @Override
        public Item value() {
            return value;
        }

        public int getIndex() {
            return index;
        }
        
        public ElementMatcher.Result mismatchResult() {
            if (mismatchResult == null) {
                mismatchResult = mismatch.matchResult(this);
            }
            return mismatchResult;
        }
    }
    
    protected static class PreviousMatcher<Value> {
        
        final ElementMatcher<? super Value> matcher;
        boolean success = true;
        PreviousMatcher next = null;

        public PreviousMatcher(ElementMatcher<? super Value> matcher) {
            this.matcher = matcher;
        }
    }
}
