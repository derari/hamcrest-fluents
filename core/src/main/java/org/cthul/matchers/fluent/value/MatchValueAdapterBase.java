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
import org.cthul.matchers.fluent.value.ElementMatcher.Element;
import org.cthul.matchers.fluent.value.ElementMatcher.ExpectationDescription;
import org.cthul.proc.Proc;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.SelfDescribing;

/**
 *
 */
public abstract class MatchValueAdapterBase<Value, Property> 
                extends SelfDescribingBase
                implements MatchValueAdapter<Value, Property> {

    @Override
    public <Value0> MatchValueAdapter<Value0, Property> adapt(MatchValueAdapter<Value0, ? extends Value> adapter) {
        return new CombinedMatchValueAdapter<>(adapter, this);
    }

    @Override
    public Matcher<Value> adapt(Matcher<? super Property> matcher) {
        return new AdaptingMatcher<>(this, matcher);
    }
    
    @Override
    public <Property2> MatchValueAdapter<Value, Property2> get(MatchValueAdapter<? super Property, Property2> adapter) {
        return adapter.adapt(this);
    }
    
    protected boolean hasDescription() {
        return true;
    }

    protected void describeConsumer(SelfDescribing consumer, Description description) {
        if (hasDescription()) {
            description.appendDescriptionOf(this)
                       .appendText(" ");
        }
        description.appendDescriptionOf(consumer);
    }

    protected void describeProducer(SelfDescribing producer, Description description) {
        if (hasDescription()) {
            description.appendDescriptionOf(this)
                       .appendText(" of ");
        }
        description.appendDescriptionOf(producer);
    }
    
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
//            description.appendDescriptionOf(mva1)
//                    .appendText(" ")
//                    .appendDescriptionOf(mva2);
        }
        
//
//        @Override
//        public void describeProducer(final SelfDescribing producer, Description description) {
//            SelfDescribing producerDescriptor = new SelfDescribingBase() {
//                @Override
//                public void describeTo(Description description) {
//                    mva1.describeProducer(producer, description);
//                }
//            };
//            mva2.describeProducer(producerDescriptor, description);
//        }
//
//        @Override
//        public void describeConsumer(final SelfDescribing consumer, Description description) {
//            SelfDescribing consumerDescriptor = new SelfDescribingBase() {
//                @Override
//                public void describeTo(Description description) {
//                    mva2.describeProducer(consumer, description);
//                }
//            };
//            mva1.describeProducer(consumerDescriptor, description);
//        }        
    }
    
    protected static class SingleElementValue<Value> 
                    extends MatchValueBase<Value>
                    implements Element<Object> {

        private final Object value;
        private ElementMatcher<? super Value> failed = null;
        private ElementMatcher<? super Value> last = null;
        private ElementMatcher.Result<?> internResult = null;
        private Result matchResult = null;
        private String valueType = null;

        public SingleElementValue(Object value) {
            this.value = value;
        }

        @Override
        public boolean matches(ElementMatcher<? super Value> matcher) {
            if (failed != null) return false;
            internResult = null;
            if (matcher.matches(this)) {
                last = matcher;
                return true;
            }
            last = null;
            failed = matcher;
            return false;
        }
        
        @Override
        public boolean matched() {
            return failed == null;
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
                if (failed != null) {
                    internResult = failed.matchResult(this);
                } else if (last != null) {
                    internResult = last.matchResult(this);
                } else {
                    throw new IllegalStateException("no result yet");
//                    result = 
                }
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

        @Override
        public Object value() {
            return value;
        }
        
        protected class Result extends MatchResultProxy<Value, Matcher<?>> {

            public Result() {
                super(null);
            }

            @Override
            public Value getValue() {
                return (Value) value;
            }

            @Override
            protected ElementMatcher.Result<?> result() {
                return SingleElementValue.this.result();
            }

            @Override
            protected ElementMatcher.Mismatch<?> mismatch() {
                return (ElementMatcher.Mismatch<?>) super.mismatch();
            }

            @Override
            public void describeExpected(Description d) {
                if (d instanceof ExpectationDescription) {
                    mismatch().describeExpected((ExpectationDescription) d);
                } else {
                    Expectation ex = new Expectation();
                    mismatch().describeExpected(ex);
                    ex.describeTo(d);
                }
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
