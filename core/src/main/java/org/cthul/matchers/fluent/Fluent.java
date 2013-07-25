package org.cthul.matchers.fluent;

import org.cthul.matchers.fluent.value.MatchValueAdapter;
import org.hamcrest.Matcher;

/**
 * Fluent that matches a value.
 * <p>
 * As a {@linkplain FluentProperty property}, it matches against the
 * value itself.
 * 
 * @param <Value> type of value
 */
public interface Fluent<Value> extends FluentProperty<Value, Value> {

    /**
     * {@inheritDoc}
     * @return this
     */
    @Override
    Fluent<Value> is();
    
    /**
     * {@inheritDoc}
     * @return this
     */
    @Override
    Fluent<Value> has();

    /**
     * {@inheritDoc}
     * @return this
     */
    @Override
    Fluent<Value> not();
    
    /**
     * Adds a matcher to the fluent.
     * Actual behavior is implementation specific.
     * @param matcher
     * @return this
     */
    @Override
    Fluent<Value> _(Matcher<? super Value> matcher);

    /**
     * {@inheritDoc}
     * @return this
     */
    @Override
    Fluent<Value> is(Matcher<? super Value> matcher);

    /**
     * {@inheritDoc}
     * @return this
     */
    @Override
    Fluent<Value> has(Matcher<? super Value> matcher);

    /**
     * {@inheritDoc}
     * @return this
     */
    @Override
    Fluent<Value> not(Matcher<? super Value> matcher);

    /**
     * {@inheritDoc}
     * @return this
     */
    @Override
    Fluent<Value> isNot(Matcher<? super Value> matcher);

    /**
     * {@inheritDoc}
     * @return this
     */
    @Override
    Fluent<Value> hasNot(Matcher<? super Value> matcher);

    /**
     * {@inheritDoc}
     * @return this
     */
    @Override
    Fluent<Value> all(Matcher<? super Value>... matchers);
    
    /**
     * {@inheritDoc}
     * @return this
     */
    @Override
    Fluent<Value> any(Matcher<? super Value>... matchers);
    
    /**
     * {@inheritDoc}
     * @return this
     */
    @Override
    Fluent<Value> none(Matcher<? super Value>... matchers);
    
    /**
     * {@inheritDoc}
     */
    @Override
    <P> FluentProperty<Value, P> _(MatchValueAdapter<? super Value, P> adapter);
    
    /**
     * {@inheritDoc}
     */
    @Override
    <P> FluentProperty<Value, P> has(MatchValueAdapter<? super Value, P> adapter);
    
    /**
     * {@inheritDoc}
     */
    @Override
    <P> FluentProperty<Value, P> not(MatchValueAdapter<? super Value, P> adapter);
    
    /**
     * {@inheritDoc}
     */
    @Override
    <P> FluentProperty<Value, P> hasNot(MatchValueAdapter<? super Value, P> adapter);
    
    /**
     * {@inheritDoc}
     */
    @Override
    Both<Value, Value> both(Matcher<? super Value> matcher);
    
    /**
     * Adds a matcher to the fluent that matches only instances of {@code clazz}
     * that are also matched by {@code matcher}, and changes the type of this
     * fluent to {@code Value2}.
     * @param <Value2> expected type
     * @param clazz expected type
     * @param matcher the matcher
     * @return fluent
     */
    @Override
    <Value2 extends Value> Fluent<Value2> isA(Class<Value2> clazz, Matcher<? super Value2> matcher);
    
    /**
     * Immediately adds a matcher to the fluent that matches only 
     * instances of {@code clazz}, and changes the type of this
     * fluent to {@code Value2}.
     * @param <Value2> expected type
     * @param clazz expected type
     * @return isA fluent
     */
    @Override
    <Value2 extends Value> FluentProperty.IsA<Value2, Value2> isA(Class<Value2> clazz);
}
