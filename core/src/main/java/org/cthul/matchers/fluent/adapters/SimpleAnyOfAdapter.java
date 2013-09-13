package org.cthul.matchers.fluent.adapters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.cthul.matchers.diagnose.QuickDiagnose;
import org.cthul.matchers.diagnose.result.MatchResult;
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
 * Base class for adapters that return muliple items from a source value,
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
            return new AnyItemIterable<>(getElements(key.value()));
        }

        @Override
        protected boolean matchSafely(Element<V> element, ElementMatcher<? super Item> matcher) {
            PreviousMatcher<Item> pm = addToPreviousChain(matcher);
            AnyItemIterable<Item> it = cachedItem(element);
            E<Item> e = it.firstValid();
            if (e == null) return false;
            if (matcher.matches(e)) {
                return true;
            }
            pm.success = false;
            e.mismatch = matcher;
            e = it.nextValid(e);
            while (e != null) {
                boolean match = matchAgainstPrevious(e);
                if (match) return true;
                e = it.nextValid(e);
            }
            return false;
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
        protected <I extends Element<V>> Result<I> matchResultSafely(I element, ElementMatcher<V> adaptedMatcher, ElementMatcher<? super Item> matcher) {
            PreviousMatcher<Item> pm = addToPreviousChain(matcher);
            AnyItemIterable<Item> it = cachedItem(element);
            E<Item> e = it.firstValid();
            if (e == null) {
                return failResult(element, adaptedMatcher, it);
            }
            if (matcher.matches(e)) {
                return successResult(element, adaptedMatcher, e);
            }
            pm.success = false;
            e.mismatch = matcher;
            e = it.nextValid(e);
            while (e != null) {
                boolean match = matchAgainstPrevious(e);
                if (match) {
                    
                }
                e = it.nextValid(e);
            }
            return failResult(element, adaptedMatcher, it);
        }
        
//        protected <I extends Element<V>> Result<I> emptyResult(I element, ElementMatcher<V> adaptedMatcher) {
//            return new MatchResultMismatch<I, Matcher<?>>(element, adaptedMatcher) {
//                @Override
//                public void describeMismatch(Description d) {
//                    d.appendText("empty");
//                }
//            };
//        }
//        
        protected <I extends Element<V>> Result<I> successResult(I element, ElementMatcher<V> adaptedMatcher, final E<Item> e) {
            final List<MatchResult.Match<?>> matches = new ArrayList<>();
            for (PreviousMatcher<Item> pm = matchers; pm != null; pm = pm.next) {
                matches.add(QuickDiagnose.matchResult(pm.matcher, e).getMatch());
            }
            return new MatchBase<I>(element, adaptedMatcher) {
                @Override
                public void describeMatch(Description d) {
                    describeElement(getValue().value(), e.value, e.i, d);
                    boolean first = true;
                    for (MatchResult.Match<?> m: matches) {
                        if (first) {
                            first = false;
                        } else {
                            d.appendText(" and ");
                        }
                        m.describeMatch(d);
                    }
                }
            };
        }
        
        protected <I extends Element<V>> Result<I> failResult(I element, ElementMatcher<V> adaptedMatcher, final AnyItemIterable<Item> it) {            
            return new MismatchBase<I>(element, adaptedMatcher) {
                @Override
                public void describeExpected(ExpectationDescription d) {
                    for (E<Item> e = it.first(); e != null; e = it.next(e)) {
                        e.mismatchResult().describeExpected(d);
                    }
                }
                @Override
                public void describeMismatch(Description d) {
                    V value = getValue().value();
                    E<Item> first = it.first();
                    E<Item> e = first;
                    while (e != null) {
                        if (e != first) {
                            if (e.next == null) {
                                d.appendText(e == first.next ? " " : ", ");
                                d.appendText("and ");
                            } else {
                                d.appendText(", ");
                            }
                        }
                        describeElement(value, e.value(), e.i, d);
                        e.mismatchResult().describeMismatch(d);
                        e = it.next(e);
                    }
                }
            };
        }

        @Override
        public void describeValue(Description description) {
            SimpleAnyOfAdapter.this.describeValue(getActualValue(), description);
        }

        @Override
        public void describeValueType(Description description) {
            SimpleAnyOfAdapter.this.describeValueType(getActualValue(), description);
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
    
    protected static class AnyItemIterable<Item> {
        
        private final Iterator<? extends Item> source;
        private E<Item> first = null;
        E<Item> firstValid = null;
        
        public AnyItemIterable(Iterable<? extends Item> iterable) {
            this.source = iterable.iterator();
        }
        
        public E<Item> firstValid() {
            if (firstValid == null) {
                if (first != null) {
                    // no valid elements
                    return null;
                }
                firstValid = first();
            }
            return firstValid;
        }
        
        public E<Item> nextValid(E<Item> e) {
            return firstValid = next(e);
        }
        
        public E<Item> first() {
            if (first == null) {
                if (!source.hasNext()) return null;
                first = new E<>(source.next(), 0);
            }
            return first;
        }
        
        public E<Item> next(E<Item> e) {
            if (e.next == null) {
                if (!source.hasNext()) return null;
                e.next = new E<>(source.next(), e.i+1);
            }
            return e.next;
        }
    }
    
    protected static class E<Item> implements Element<Item> {
        
        private final Item value;
        final int i;
        ElementMatcher<? super Item> mismatch = null;
        E<Item> next = null;
        ElementMatcher.Mismatch<E<Item>> mismatchResult = null;

        public E(Item value, int i) {
            this.value = value;
            this.i = i;
        }

        @Override
        public Item value() {
            return value;
        }
        
        public ElementMatcher.Mismatch<E<Item>> mismatchResult() {
            if (mismatchResult == null) {
                mismatchResult = mismatch.matchResult(this).getMismatch();
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
