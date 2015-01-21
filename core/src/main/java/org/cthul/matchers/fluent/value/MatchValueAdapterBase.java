package org.cthul.matchers.fluent.value;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.cthul.matchers.diagnose.SelfDescribingBase;
import org.cthul.matchers.diagnose.nested.Nested;
import org.cthul.matchers.diagnose.nested.NestedResultMatcher;
import org.cthul.matchers.diagnose.nested.PrecedencedSelfDescribing;
import org.cthul.matchers.diagnose.nested.PrecedencedSelfDescribingBase;
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
        consumer.describeTo(description);
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
        producer.describeTo(description);
    }
    
    /**
     * Describes a result produced by this adapter.
     * <p>
     * By default, if this adapter {@linkplain  #hasDescription() has a description},
     * it is written to {@code description} first.
     * @param result
     * @param description 
     */
    protected void describeResult(SelfDescribing result, Description description) {
        if (hasDescription()) {
            description.appendDescriptionOf(this)
                       .appendText(" ");
        }
        result.describeTo(description);
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
                    implements Element<Object>,
                        MatchResult.Match<Value>, MatchResult.Mismatch<Value> {

        private static final ElementMatcher<Object> ANYTHING = new ElementMatcherWrapper<>(-1, IsAnything.anything("anything"));
        
        private final Object value;
        private boolean valid = true;
        private ElementMatcher<? super Value> last = ANYTHING;
        private ElementMatcher.Result internResult = null;
        private String valueType = null;

        public SingleElementValue(Object value) {
            this.value = value;
        }
        
        public SingleElementValue(Object value, String valueType) {
            this.value = value;
            this.valueType = valueType;
        }

        @Override
        public boolean matches(ElementMatcher<? super Value> matcher) {
            if (!valid) return false;
            valid = matcher.matches(this);
            last = matcher;
            internResult = null;
            return valid;
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
            description.appendText(getValueType());
        }

        public String getValueType() {
            if (valueType == null) {
                valueType = detectValueType();
            }
            return valueType;
        }
        
        protected String detectValueType() {
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

        private ElementMatcher.Result result() {
            if (internResult == null) {
                internResult = last.matchResult(this);
            }
            return internResult;
        }
        
        @Override
        public MatchResult<?> matchResult() {
            return this;
        }

        /**
         * Implements the {@link Element} interface.
         * @return element value
         */
        @Override
        public Object value() {
            return value;
        }
        
        // MatchResult interfaces implemented below

        @Override
        public Value getValue() {
            return (Value) value;
        }

        @Override
        public void describeTo(Description description) {
            if (last == ANYTHING) {
                super.describeTo(description);
            } else {
                result().describeTo(description);
            }
        }
        
        @Override
        public int getDescriptionPrecedence() {
            return P_ATOMIC;
        }

        @Override
        public Matcher<?> getMatcher() {
            return ElementMatcherWrapper.asMatcher(last);
        }

        @Override
        public void describeMatcher(Description description) {
            last.describeTo(description);
        }

        @Override
        public int getMatcherPrecedence() {
            return last.getDescriptionPrecedence();
        }

        @Override
        public PrecedencedSelfDescribing getMatcherDescription() {
            return last;
        }

        @Override
        public Match<Value> getMatch() {
            return matched() ? this : null;
        }

        @Override
        public void describeMatch(Description description) {
            describeTo(description);
        }

        @Override
        public PrecedencedSelfDescribing getMatchDescription() {
            return this;
        }

        @Override
        public int getMatchPrecedence() {
            return getDescriptionPrecedence();
        }

        @Override
        public Mismatch<Value> getMismatch() {
            return matched() ? null : this;
        }

        @Override
        public void describeExpected(Description description) {
            ExpectationStringDescription exp = new ExpectationStringDescription();
            result().describeExpected(exp);
            exp.describeTo(description);
        }

        @Override
        public int getExpectedPrecedence() {
            return getDescriptionPrecedence();
        }

        @Override
        public PrecedencedSelfDescribing getExpectedDescription() {
            return new PrecedencedSelfDescribingBase() {
                @Override
                public void describeTo(Description description) {
                    describeExpected(description);
                }
                @Override
                public int getDescriptionPrecedence() {
                    return getExpectedPrecedence();
                }
            };
        }

        @Override
        public void describeMismatch(Description description) {
            describeTo(description);
        }

        @Override
        public int getMismatchPrecedence() {
           return getDescriptionPrecedence();
        }

        @Override
        public PrecedencedSelfDescribing getMismatchDescription() {
            return this;
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
            MatchValue<Property> mv = adapter.adapt(item);
            mv.matches(matcher);
            return new AdaptedResult<>(mv.matchResult(), item);
        }
        
        class AdaptedResult<I> extends MatchResultProxy<I, AdaptingMatcher<?, ?>> {

            public AdaptedResult(MatchResult<?> result, I value) {
                super(result, value, AdaptingMatcher.this);
            }

            @Override
            protected boolean fastProxy() {
                return false;
            }

            @Override
            public void describeMatch(Description description) {
                adapter.describeResult(match().getMatchDescription(), description);
            }

            @Override
            public void describeExpected(Description description) {
                adapter.describeConsumer(mismatch().getExpectedDescription(), description);
            }

            @Override
            public void describeMismatch(Description description) {
                adapter.describeResult(mismatch().getMismatchDescription(), description);
            }
        }
    }
    
    protected static abstract class InternalResult extends SelfDescribingBase implements ElementMatcher.Result {
        private final boolean match;

        public InternalResult() {
            this.match = false;
        }

        public InternalResult(boolean match) {
            this.match = match;
        }

        @Override
        public boolean matched() {
            return match;
        }
    }
}
