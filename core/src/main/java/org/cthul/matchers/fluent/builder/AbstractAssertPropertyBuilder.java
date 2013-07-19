package org.cthul.matchers.fluent.builder;

import org.cthul.matchers.fluent.FluentAssert;
import org.cthul.matchers.fluent.property.FluentAssertProperty;
import org.cthul.matchers.fluent.value.AdaptingMatcher;
import org.cthul.matchers.fluent.value.MatchValueAdapter;
import org.hamcrest.Matcher;

/**
 *
 */
public abstract class AbstractAssertPropertyBuilder
                <Value, Property, ThisFluent extends FluentAssert<Value>,
                 This extends FluentAssertProperty<Value, Property>>
                extends AbstractFluentPropertyBuilder<Value, Property, ThisFluent, This>
                implements FluentAssertProperty<Value, Property> {

    @Override
    protected <P> FluentAssertProperty<Value, P> _newProperty(MatchValueAdapter<? super Property, P> adapter, String prefix, boolean not) {
        return new AssertPropertyBuilder<>(adapter, prefix, not);
    }

    @Override
    public <P> FluentAssertProperty<Value, P> _(MatchValueAdapter<? super Property, P> adapter) {
        return (FluentAssertProperty) super._(adapter);
    }
    
    @Override
    public <P> FluentAssertProperty<Value, P> has(MatchValueAdapter<? super Property, P> adapter) {
        return (FluentAssertProperty) super.has(adapter);
    }
    
    @Override
    public <P> FluentAssertProperty<Value, P> not(MatchValueAdapter<? super Property, P> adapter) {
        return (FluentAssertProperty) super.not(adapter);
    }
    
    @Override
    public <P> FluentAssertProperty<Value, P> hasNot(MatchValueAdapter<? super Property, P> adapter) {
        return (FluentAssertProperty) super.hasNot(adapter);
    }

    @Override
    public <Property2 extends Property> FluentAssert<? extends Value> isA(Class<Property2> clazz, Matcher<? super Property2> matcher) {
        return (FluentAssert) super.isA(clazz, matcher);
    }

    @Override
    public <Property2 extends Property> FluentAssertProperty.IsA<? extends Value, Property2> isA(Class<Property2> clazz) {
        return (FluentAssertProperty.IsA) super.isA(clazz);
    }

    @Override
    protected Class<?> getIsAInterface() {
        return FluentAssertProperty.IsA.class;
    }
    
    protected class AssertPropertyBuilder
                    <Property2, This2 extends AssertPropertyBuilder<Property2, This2>>
                    extends AbstractAssertPropertyBuilder<Value, Property2, ThisFluent, This2> 
                    implements FluentAssertProperty<Value, Property2> {
        
        private final MatchValueAdapter<? super Property, Property2> adapter;
        private final String flPrefix;
        private final boolean flNot;

        public AssertPropertyBuilder(MatchValueAdapter<? super Property, Property2> adapter, String prefix, boolean not) {
            this.adapter = adapter;
            this.flPrefix = prefix;
            this.flNot = not;
        }

        @Override
        protected ThisFluent _applyMatcher(Matcher<? super Property2> matcher, String prefix, boolean not) {
            Matcher<? super Property> m = new AdaptingMatcher<>(adapter, matcher, prefix, not);
            return AbstractAssertPropertyBuilder.this._applyMatcher(m, flPrefix, flNot);
        }      
    }
    
//    protected static class IsA<Value, Property,
//                        ThisFluent extends FluentAssert<Value>,
//                        ThisProperty extends FluentAssertProperty<Value, Property>> 
//                    extends AbstractAssertPropertyBuilder<Value, Value, ThisFluent, ThisFluent>
//                    implements FluentAssertProperty.IsA<Value, Property> {
//
//        
//        @Override
//        public <Value2 extends Value> FluentAssert<Value2> isA(Class<Value2> clazz, Matcher<? super Value2> matcher) {
//            return (FluentAssert<Value2>) super.isA(clazz, matcher);
//        }
//
//        @Override
//        public <Value2 extends Value> FluentAssert.IsA<Value2> isA(Class<Value2> clazz) {
//            return null; 
//        }
//        
//    }
    
}
