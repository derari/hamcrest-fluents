package org.cthul.matchers.fluent;

import org.cthul.matchers.fluent.value.MatchValueAdapter;
import org.hamcrest.Matcher;

/**
 * Fluent assert that matches a property of a value.
 * After the match, the chain goes back to the value itself.
 * 
 * @param <Value, Property> 
 */
public interface FluentPropertyAssert<Value, Property> extends FluentProperty<Value, Property> {

    @Override
    FluentPropertyAssert<Value, Property> as(String reason);

    @Override
    FluentPropertyAssert<Value, Property> as(String reason, Object... args);

    @Override
    FluentPropertyAssert<Value, Property> is();

    @Override
    FluentPropertyAssert<Value, Property> has();

    @Override
    FluentPropertyAssert<Value, Property> not();
    
    @Override
    FluentAssert<Value> _(Matcher<? super Property> matcher);

    @Override
    FluentAssert<Value> is(Matcher<? super Property> matcher);
    
    @Override
    FluentAssert<Value> has(Matcher<? super Property> matcher);

    @Override
    FluentAssert<Value> not(Matcher<? super Property> matcher);
    
    @Override
    FluentAssert<Value> isNot(Matcher<? super Property> matcher);
    
    @Override
    FluentAssert<Value> hasNot(Matcher<? super Property> matcher);

    @Override
    FluentAssert<Value> all(Matcher<? super Property>... matcher);
    
    @Override
    FluentAssert<Value> any(Matcher<? super Property>... matcher);
    
    @Override
    FluentAssert<Value> none(Matcher<? super Property>... matcher);
    
    @Override
    <P> FluentPropertyAssert<Value, P> _(MatchValueAdapter<? super Property, P> matcher);
    
    @Override
    <P> FluentPropertyAssert<Value, P> has(MatchValueAdapter<? super Property, P> adapter);
    
    @Override
    <P> FluentPropertyAssert<Value, P> not(MatchValueAdapter<? super Property, P> adapter);
    
    @Override
    <P> FluentPropertyAssert<Value, P> hasNot(MatchValueAdapter<? super Property, P> adapter);
    
    @Override
    <Property2 extends Property> FluentAssert<? extends Value> isA(Class<Property2> clazz, Matcher<? super Property2> matcher);
    
    @Override
    <Property2 extends Property> IsA<? extends Value, Property2> isA(Class<Property2> clazz);
    
    interface IsA<Value, Property> 
                    extends FluentProperty.IsA<Value, Property>,
                            FluentAssert<Value> {

        @Override
        FluentPropertyAssert<Value, Property> that();
        
        @Override
        FluentPropertyAssert<Value, Property> thatIs();

        @Override
        FluentAssert<Value> that(Matcher<? super Property> matcher);
        
        @Override
        FluentAssert<Value> thatIs(Matcher<? super Property> matcher);

    }
}