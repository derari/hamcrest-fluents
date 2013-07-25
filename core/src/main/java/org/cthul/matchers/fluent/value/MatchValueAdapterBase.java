package org.cthul.matchers.fluent.value;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.cthul.proc.Proc;
import org.hamcrest.Description;
import org.hamcrest.SelfDescribing;

/**
 *
 */
public abstract class MatchValueAdapterBase<Value, Property> 
                extends SelfDescribingBase
                implements MatchValueAdapter<Value, Property> {

    @Override
    public <Value0> MatchValueAdapter<Value0, Property> adapt(MatchValueAdapter<Value0, Value> adapter) {
        return new CombinedMatchValueAdapter<>(adapter, this);
    }
    
    protected boolean hasDescription() {
        return true;
    }

    @Override
    public void describeConsumer(SelfDescribing consumer, Description description) {
        if (hasDescription()) {
            description.appendDescriptionOf(this)
                       .appendText(" ");
        }
        description.appendDescriptionOf(consumer);
    }

    @Override
    public void describeProducer(SelfDescribing producer, Description description) {
        if (hasDescription()) {
            description.appendDescriptionOf(this)
                       .appendText(" of ");
        }
        description.appendDescriptionOf(producer);
    }
    
    protected static class CombinedMatchValueAdapter<Value, Tmp, Property> extends MatchValueAdapterBase<Value, Property> {
        
        private final MatchValueAdapter<Value, Tmp> mva1;
        private final MatchValueAdapter<Tmp, Property> mva2;

        public CombinedMatchValueAdapter(MatchValueAdapter<Value, Tmp> mva1, MatchValueAdapter<Tmp, Property> mva2) {
            this.mva1 = mva1;
            this.mva2 = mva2;
        }

        @Override
        public MatchValue<Property> adapt(Object value) {
            return mva2.adapt(mva1.adapt(value));
        }

        @Override
        public MatchValue<Property> adapt(MatchValue<Value> value) {
            return mva2.adapt(mva1.adapt(value));
        }

        @Override
        public void describeTo(Description description) {
            description.appendDescriptionOf(mva1)
                    .appendText(" ")
                    .appendDescriptionOf(mva2);
        }

        @Override
        public void describeProducer(final SelfDescribing producer, Description description) {
            SelfDescribing producerDescriptor = new SelfDescribingBase() {
                @Override
                public void describeTo(Description description) {
                    mva1.describeProducer(producer, description);
                }
            };
            mva2.describeProducer(producerDescriptor, description);
        }

        @Override
        public void describeConsumer(final SelfDescribing consumer, Description description) {
            SelfDescribing consumerDescriptor = new SelfDescribingBase() {
                @Override
                public void describeTo(Description description) {
                    mva2.describeProducer(consumer, description);
                }
            };
            mva1.describeProducer(consumerDescriptor, description);
        }        
    }
    
    protected static class SingleElementValue<Value> 
                    extends MatchValueBase<Value>
                    implements MatchValue.Element<Object> {

        private final Object value;
        private MatchValue.ElementMatcher<Value> mismatch = null;
        private String valueType = null;

        public SingleElementValue(Object value) {
            this.value = value;
        }

        @Override
        public boolean matches(MatchValue.ElementMatcher<Value> matcher) {
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

        @Override
        public void describeExpected(MatchValue.ExpectationDescription description) {
            mismatch.describeExpected(this, description);
        }

        @Override
        public void describeMismatch(Description description) {
            mismatch.describeMismatch(this, description);
        }

        @Override
        public Object value() {
            return value;
        }
    }
}
