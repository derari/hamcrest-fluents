package org.cthul.matchers.fluent;

/**
 * A {@link FluentStep} that always returns to itself.
 * Base interface for fluent matcher chains;
 * see {@link FluentMatcher} and {@link FluentAssert} for concrete use-cases.
 * 
 * @param <Value> value type
 */
public interface Fluent<Value> extends FluentStep<Value, Fluent<Value>> {

    /**
     * Prepends "is" to the next matcher's description.
     * @return this
     */
    @Override
    Fluent<Value> is();

    /**
     * Prepends "has" to the next matcher's description.
     * @return this
     */
    @Override
    Fluent<Value> has();

    /**
     * Negates the next matcher's match result and 
     * prepends "not" to its description.
     * @return this
     */
    @Override
    Fluent<Value> not();
}
