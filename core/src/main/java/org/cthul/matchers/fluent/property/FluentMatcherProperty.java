package org.cthul.matchers.fluent.property;

import org.cthul.matchers.fluent.*;
import org.cthul.matchers.fluent.value.MatchValueAdapter;
import org.hamcrest.Matcher;

/**
 * Fluent Matcher that matches a property of a value.
 * After the match, the chain goes back to the value itself.
 * 
 * @param <Value, Property> 
 */
public interface FluentMatcherProperty<Value, Property, Match> 
                extends FluentProperty<Value, Property> {

    @Override
    FluentMatcherProperty<Value, Property, Match> as(String reason);

    @Override
    FluentMatcherProperty<Value, Property, Match> as(String reason, Object... args);

    @Override
    FluentMatcherProperty<Value, Property, Match> is();

    @Override
    FluentMatcherProperty<Value, Property, Match> has();

    @Override
    FluentMatcherProperty<Value, Property, Match> not();
    
    @Override
    FluentMatcher<Value, Match> _(Matcher<? super Property> matcher);

    @Override
    FluentMatcher<Value, Match> is(Matcher<? super Property> matcher);
    
    @Override
    FluentMatcher<Value, Match> has(Matcher<? super Property> matcher);

    @Override
    FluentMatcher<Value, Match> not(Matcher<? super Property> matcher);
    
    @Override
    FluentMatcher<Value, Match> isNot(Matcher<? super Property> matcher);
    
    @Override
    FluentMatcher<Value, Match> hasNot(Matcher<? super Property> matcher);

    @Override
    FluentMatcher<Value, Match> all(Matcher<? super Property>... matcher);
    
    @Override
    FluentMatcher<Value, Match> any(Matcher<? super Property>... matcher);
    
    @Override
    FluentMatcher<Value, Match> none(Matcher<? super Property>... matcher);
    
    @Override
    <P> FluentMatcherProperty<Value, P, Match> _(MatchValueAdapter<? super Property, P> matcher);
    
    @Override
    <P> FluentMatcherProperty<Value, P, Match> has(MatchValueAdapter<? super Property, P> adapter);
    
    @Override
    <P> FluentMatcherProperty<Value, P, Match> not(MatchValueAdapter<? super Property, P> adapter);
    
    @Override
    <P> FluentMatcherProperty<Value, P, Match> hasNot(MatchValueAdapter<? super Property, P> adapter);
    
    @Override
    <Property2 extends Property> FluentMatcher<? extends Value, Match> isA(Class<Property2> clazz, Matcher<? super Property2> matcher);
    
    @Override
    <Property2 extends Property> IsA<? extends Value, Property2, Match> isA(Class<Property2> clazz);
    
    interface IsA<Value, Property, Match> 
                    extends FluentProperty.IsA<Value, Property>,
                            FluentMatcher<Value, Match> {

        @Override
        FluentMatcherProperty<Value, Property, Match> that();
        
        @Override
        FluentMatcherProperty<Value, Property, Match> thatIs();

        @Override
        FluentMatcher<Value, Match> that(Matcher<? super Property> matcher);

        @Override
        FluentMatcher<Value, Match> thatIs(Matcher<? super Property> matcher);
    }
}