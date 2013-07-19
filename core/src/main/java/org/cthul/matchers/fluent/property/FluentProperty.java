package org.cthul.matchers.fluent.property;

import org.cthul.matchers.fluent.Fluent;
import org.cthul.matchers.fluent.value.MatchValueAdapter;
import org.hamcrest.Matcher;


/**
 * Fluent that matches a property of a value.
 * After the match, the chain goes back to the value itself.
 * 
 * @param <Value, Property> 
 */
public interface FluentProperty<Value, Property> {

    FluentProperty<Value, Property> as(String reason);

    FluentProperty<Value, Property> as(String reason, Object... args);

    FluentProperty<Value, Property> is();

    FluentProperty<Value, Property> has();

    FluentProperty<Value, Property> not();
    
    Fluent<Value> _(Matcher<? super Property> matcher);

    Fluent<Value> is(Matcher<? super Property> matcher);
    
    Fluent<Value> has(Matcher<? super Property> matcher);

    Fluent<Value> not(Matcher<? super Property> matcher);
    
    Fluent<Value> isNot(Matcher<? super Property> matcher);
    
    Fluent<Value> hasNot(Matcher<? super Property> matcher);

    Fluent<Value> all(Matcher<? super Property>... matchers);
    
    Fluent<Value> any(Matcher<? super Property>... matchers);
    
    Fluent<Value> none(Matcher<? super Property>... matchers);
    
    <P> FluentProperty<Value, P> _(MatchValueAdapter<? super Property, P> adapter);
    
    <P> FluentProperty<Value, P> has(MatchValueAdapter<? super Property, P> adapter);
    
    <P> FluentProperty<Value, P> not(MatchValueAdapter<? super Property, P> adapter);
    
    <P> FluentProperty<Value, P> hasNot(MatchValueAdapter<? super Property, P> adapter);
    
    // Both<Value, Propert> both(Matcher<? super Property> matcher);
    
    // Either<Value, Propert> either(Matcher<? super Property>... matchers);
    
    // Neither<Value, Propert> neither(Matcher<? super Property>... matchers);
    
    interface Both<Value, Property> {
        
        Fluent<Value> and(Matcher<? super Property> matcher);
        
    }
    
    interface Either<Value, Property> {
        
        Fluent<Value> or(Matcher<? super Property> matcher);
        
        Fluent<Value> xor(Matcher<? super Property> matcher);
        
    }
    
    interface Neither<Value, Property> {
        
        Fluent<Value> nor(Matcher<? super Property> matcher);
        
    }
    
    <Property2 extends Property> Fluent<? extends Value> isA(Class<Property2> clazz, Matcher<? super Property2> matcher);
    
    <Property2 extends Property> IsA<? extends Value, Property2> isA(Class<Property2> clazz);
    
    interface IsA<Value, Property> extends Fluent<Value> {

        FluentProperty<Value, Property> that();
        
        FluentProperty<Value, Property> thatIs();

        Fluent<Value> that(Matcher<? super Property> matcher);

        Fluent<Value> thatIs(Matcher<? super Property> matcher);
        
    }
    
}
