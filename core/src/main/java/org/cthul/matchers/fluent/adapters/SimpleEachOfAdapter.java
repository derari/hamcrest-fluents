package org.cthul.matchers.fluent.adapters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.cthul.matchers.diagnose.result.MatchResult;
import org.cthul.matchers.fluent.value.AbstractMatchValueAdapter;
import org.cthul.matchers.fluent.value.MatchValue;
import org.cthul.matchers.fluent.value.ElementMatcher;
import org.cthul.matchers.fluent.value.ElementMatcher.Element;
import org.cthul.matchers.fluent.value.ElementMatcher.ExpectationDescription;
import org.cthul.matchers.fluent.value.ElementMatcher.Mismatch;
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
            return new EachItemIterable<>(getElements(key.value()));
        }

        @Override
        protected boolean matchSafely(Element<V> element, ElementMatcher<? super Item> matcher) {
            EachItemIterable<Item> it = cachedItem(element);
            if (it.invalid != null) return false;
            E<Item> e = it.first();
            while (e != null) {
                if (!matcher.matches(e)) {
                    e.mismatch = matcher;
                    it.invalid = e;
                    return false;
                }
                e = it.next(e);
            }
            return true;
        }

        @Override
        protected <I extends Element<V>> Result<I> matchResultSafely(I element, ElementMatcher<V> adaptedMatcher, ElementMatcher<? super Item> matcher) {
            EachItemIterable<Item> it = cachedItem(element);
            if (it.invalid != null) {
                E<Item> invalid = it.invalid;
                return failResult(element, adaptedMatcher, invalid.mismatch.matchResult(invalid).getMismatch());
            }
            List<MatchResult.Match<E<Item>>> results = new ArrayList<>();
            E<Item> e = it.first();
            while (e != null) {
                Result<E<Item>> mr = matcher.matchResult(e);
                if (mr.matched()) {
                    results.add(mr.getMatch());
                } else {
                    return failResult(element, adaptedMatcher, mr.getMismatch());
                }
                e = it.next(e);
            }
            return successResult(element, adaptedMatcher, results);
        }
        
        protected <I extends Element<V>> Result<I> successResult(I element, ElementMatcher<V> adaptedMatcher, final List<MatchResult.Match<E<Item>>> matches) {
            return new MatchBase<I>(element, adaptedMatcher) {
                @Override
                public void describeMatch(Description d) {
                    V value = getValue().value();
                    boolean first = true;
                    for (MatchResult.Match<E<Item>> m: matches) {
                        if (first) {
                            first = false;
                        } else {
                            d.appendText(", ");
                        }
                        E<Item> e = m.getValue();
                        SimpleEachOfAdapter.this.describeElement(value, e.value(), e.i, d);
                        m.describeMatch(d);
                    }
                }
            };
        }
        
        protected <I extends Element<V>> Result<I> failResult(I element, ElementMatcher<V> adaptedMatcher, final Mismatch<E<Item>> mismatch) {
            return new MismatchBase<I>(element, adaptedMatcher) {
                @Override
                public void describeExpected(ExpectationDescription description) {
                    mismatch.describeExpected(description);
                }
                @Override
                public void describeMismatch(Description d) {
                    E<Item> e = mismatch.getValue();
                    SimpleEachOfAdapter.this.describeElement(getValue().value(), e.value(), e.i, d);
                    mismatch.describeMismatch(d);
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
//
//        @Override
//        protected void describeMatchSafely(Element<V> element, ElementMatcher<Item> matcher, Description description) {
//            description.appendText("each is valid");
//        }
//
//        @Override
//        protected void describeExpectedSafely(Element<V> element, ElementMatcher<Item> matcher, ExpectationDescription description) {
//            EachItemIterable<Item> it = cachedItem(element);
//            E<Item> e = it.invalid;
////          do we even care?
////            assert e.mismatch == matcher : 
////                    "The nested value should complain only about the matcher "
////                    + "that caused this one to fail";
//            matcher.describeExpected(e, description);
//        }
//
//        @Override
//        protected void describeMismatchSafely(Element<V> element, ElementMatcher<Item> matcher, Description description) {
//            EachItemIterable<Item> it = cachedItem(element);
//            E<Item> e = it.invalid;
////          do we even care?            
////            assert e.mismatch == matcher : 
////                    "The nested value should complain only about the matcher "
////                    + "that caused this one to fail";            
//            describeElement(element.value(), e.value(), e.i, description);
//            matcher.describeMismatch(e, description);
//        }        
    }
    
    protected static class EachItemIterable<Item> {
        
        private final Iterator<? extends Item> source;
        private E<Item> first = null;
        E<Item> invalid = null;
        
        public EachItemIterable(Iterable<? extends Item> iterable) {
            this.source = iterable.iterator();
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

        public E(Item value, int i) {
            this.value = value;
            this.i = i;
        }

        @Override
        public Item value() {
            return value;
        }
    }
}
