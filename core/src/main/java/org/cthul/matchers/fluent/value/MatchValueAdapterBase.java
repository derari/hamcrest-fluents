package org.cthul.matchers.fluent.value;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.cthul.matchers.diagnose.SelfDescribingBase;
import org.cthul.matchers.diagnose.nested.Nested;
import org.cthul.matchers.diagnose.nested.NestedResultMatcher;
import org.cthul.matchers.diagnose.result.MatchResult;
import org.cthul.matchers.diagnose.result.MatchResultProxy;
import org.cthul.matchers.fluent.adapters.IdentityValue;
import org.cthul.matchers.fluent.value.ElementMatcher.Element;
import org.cthul.proc.Proc;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.SelfDescribing;
import org.hamcrest.core.IsAnything;

/**
 * Default superclass for {@link MatchValueAdapter}.
 * Provides default implementations for all methods 
 * that do not implement specific logic.
 * (see also {@link AbstractMatchValueAdapter}).
 * <p>
 * All implementations should extend this class for forward compatibility.
 * @param <Value>
 * @param <Property>
 */
public abstract class MatchValueAdapterBase<Value, Property> 
                extends SelfDescribingBase
                implements MatchValueAdapter<Value, Property> {

    /**
     * {@inheritDoc}
     * @see CombinedMatchValueAdapter
     */
    @Override
    public <Value0> MatchValueAdapter<Value0, Property> adapt(MatchValueAdapter<Value0, ? extends Value> adapter) {
        return new CombinedMatchValueAdapter<>(adapter, this);
    }

    /**
     * {@inheritDoc}
     * @see AdaptingMatcher
     */
    @Override
    public Matcher<Value> adapt(Matcher<? super Property> matcher) {
        return new AdaptingMatcher<>(this, matcher);
    }

    /**
     * Delegates to {@link #adapt(MatchValue)}.
     * @see SingleElementValue 
     */
    @Override
    public MatchValue<Property> adapt(Object value) {
        return adapt(new SingleElementValue<Value>(value));
    }
    
    /**
     * Calls {@link #adapt(MatchValueAdapter) adapter.adapt(this)}.
     */
    @Override
    public <Property2> MatchValueAdapter<Value, Property2> get(MatchValueAdapter<? super Property, Property2> adapter) {
        return adapter.adapt(this);
    }
    
    /**
     * Indicates whether this adapter's description should be included when
     * describing match values or match messages.
     * @return whether this adapter has a description
     */
    protected boolean hasDescription() {
        return true;
    }

    /**
     * Describes this adapter as a producer to {@code consumer}.
     * <p>
     * By default, if this adapter {@linkplain  #hasDescription() has a description},
     * it is written to {@code description} first.
     * @param consumer
     * @param description 
     */
    protected void describeConsumer(SelfDescribing consumer, Description description) {
        if (hasDescription()) {
            description.appendDescriptionOf(this)
                       .appendText(" ");
        }
        description.appendDescriptionOf(consumer);
    }

    /**
     * Describes this adapter as a consumer of {@code producer}.
     * <p>
     * By default, if this adapter {@linkplain  #hasDescription() has a description},
     * it is written to {@code description} first, followed by {@code " of "}.
     * @param producer
     * @param description 
     */
    protected void describeProducer(SelfDescribing producer, Description description) {
        if (hasDescription()) {
            description.appendDescriptionOf(this)
                       .appendText(" of ");
        }
        description.appendDescriptionOf(producer);
    }
    
    /**
     * Adapts values with two 
     * @param <Value>
     * @param <Tmp>
     * @param <Property> 
     */
    protected static class CombinedMatchValueAdapter<Value, Tmp, Property> extends MatchValueAdapterBase<Value, Property> {
        
        private final MatchValueAdapter<Value, ? extends Tmp> mva1;
        private final MatchValueAdapterBase<Tmp, Property> mva2;

        public CombinedMatchValueAdapter(MatchValueAdapter<Value, ? extends Tmp> mva1, MatchValueAdapterBase<Tmp, Property> mva2) {
            this.mva1 = mva1;
            this.mva2 = mva2;
        }

        @Override
        public MatchValue<Property> adapt(Object value) {
            return mva2.adapt(mva1.adapt(value));
        }

        @Override
        public MatchValue<Property> adapt(MatchValue<? extends Value> value) {
            return mva2.adapt(mva1.adapt(value));
        }

        @Override
        public void describeTo(Description description) {
            mva2.describeProducer(mva1, description);
        }
    }
    
    /**
     * A match value that has a single, plain element.
     * <p>
     * If you feel you need to directly create an instance of this class,
     * use {@link IdentityValue#value(java.lang.Object)}.
     * @param <Value> 
     */
    protected static class SingleElementValue<Value> 
                    extends MatchValueBase<Value>
                    implements Element<Object> {

        private static final ElementMatcher<Object> ANYTHING = new ElementMatcherWrapper<>(-1, IsAnything.anything("anything"));
        
        private final Object value;
        private boolean valid = true;
        private ElementMatcher<? super Value> last = ANYTHING;
        private ElementMatcher.Result<?> internResult = null;
        private Result matchResult = null;
        private String valueType = null;

        public SingleElementValue(Object value) {
            this.value = value;
        }

        @Override
        public boolean matches(ElementMatcher<? super Value> matcher) {
            if (!valid) return false;
            last = matcher;
            internResult = null;
            if (matcher.matches(this)) {
                return true;
            }
            valid = false;
            return false;
        }
        
        @Override
        public boolean matched() {
            return valid;
        }

        @Override
        public void describeValue(Description description) {
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
                    } else {
                        return "iterable";
                    }
                }
                if (value instanceof Map) {
                    return "map";
                } else if (value.getClass().isArray()) {
                    return "array";
                } else if (value instanceof Proc) {
                    return "call";
                }
            }
           return "value";
        }

        private ElementMatcher.Result<?> result() {
            if (internResult == null) {
                internResult = last.matchResult(this);
            }
            return internResult;
        }
        
        @Override
        public MatchResult<?> matchResult() {
            if (matchResult == null) {
                matchResult = new Result();
            }
            return matchResult;
        }

        /**
         * Implements the {@link Element} interface.
         * @return element value
         */
        @Override
        public Object value() {
            return value;
        }
        
        /**
         * MatchResult describing the current state.
         * <p>
         * Most calls are directly delegated to {@link SingleElementValue#result()}.
         */
        protected class Result extends MatchResultProxy<Value, Matcher<?>> {

            public Result() {
                super(null);
            }

            @Override
            public Value getValue() {
                return (Value) value;
            }

            @Override
            public boolean matched() {
                return SingleElementValue.this.matched();
            }

            @Override
            protected ElementMatcher.Result<?> result() {
                return SingleElementValue.this.result();
            }

            @Override
            protected ElementMatcher.Mismatch<?> mismatch() {
                return (ElementMatcher.Mismatch<?>) super.mismatch();
            }

            /**
             * Fills an {@link ElementMatcher.ExpectationDescription ExpectationDescription} 
             * and writes it to {@code description}
             * @param description 
             */
            @Override
            public void describeExpected(Description description) {
//                if (description instanceof ExpectationDescription) {
//                    mismatch().describeExpected((ExpectationDescription) description);
//                } else {
                    ExpectationStringDescription ex = new ExpectationStringDescription();
                    mismatch().describeExpected(ex);
                    ex.describeTo(description);
//                }
            }
        }
    }
    
    protected static class AdaptingMatcher<Value, Property> extends NestedResultMatcher<Value> {

        private final ElementMatcherWrapper<Property> matcher;
        private final MatchValueAdapterBase<Value, Property> adapter;

        public AdaptingMatcher(MatchValueAdapterBase<Value, Property> adapter, Matcher<? super Property> matcher, String prefix, boolean not) {
            this.adapter = adapter;
            this.matcher = new ElementMatcherWrapper<>(-1, matcher, prefix, not);
        }

        public AdaptingMatcher(MatchValueAdapterBase<Value, Property> adapter, Matcher<? super Property> matcher) {
            this.adapter = adapter;
            this.matcher = new ElementMatcherWrapper<>(-1, matcher);
        }

        @Override
        public void describeTo(Description description) {
            adapter.describeConsumer(matcher, description);
        }

        @Override
        public int getDescriptionPrecedence() {
            return Nested.precedenceOf(matcher);
        }

        @Override
        public boolean matches(Object item) {
            return adapter.adapt(item).matches(matcher);
        }

        @Override
        public <I> MatchResult<I> matchResult(I item) {
            MatchResult<?> mr = adapter.adapt(item).matchResult(matcher);
            return new AdaptedResult<>(mr, item);
        }
        
        class AdaptedResult<I> extends MatchResultProxy<I, AdaptingMatcher<?, ?>> {

            public AdaptedResult(MatchResult<?> result, I value) {
                super(result, value, AdaptingMatcher.this);
            }

            @Override
            public void describeMatch(Description description) {
                adapter.describeConsumer(match().getMatchDescription(), description);
            }

            @Override
            public void describeExpected(Description description) {
                adapter.describeConsumer(mismatch().getExpectedDescription(), description);
            }

            @Override
            public void describeMismatch(Description description) {
                adapter.describeConsumer(mismatch().getMismatchDescription(), description);
            }
        }
    }
}
