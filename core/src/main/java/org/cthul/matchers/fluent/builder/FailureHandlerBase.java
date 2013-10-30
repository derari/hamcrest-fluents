package org.cthul.matchers.fluent.builder;

import org.cthul.matchers.fluent.value.Expectation;
import org.cthul.matchers.fluent.value.MatchValue;
import org.hamcrest.Description;
import org.hamcrest.StringDescription;

public abstract class FailureHandlerBase implements FailureHandler {

    protected String getMismatchString(String reason, MatchValue<?> actual) {
        Description description = new StringDescription();
        if (reason != null) {
            description.appendText(reason).appendText("\n");
        } else {
//            actual.describeTo(description).appendText("\n");
        }
        description.appendText("Expected: ");
        actual.describeValueType(description);
        description.appendText(" ");
        actual.matchResult().getMismatch().describeExpected(description);
        
        description.appendText("\n     but: ");
        actual.matchResult().getMismatch().describeMismatch(description);
        
        return description.toString();
    }
    
    /*
     * eachOf(primeFactorsOf(120)) 
     *      each of prime factors of <120>
     * -- is(lt(3))
     *      expected that each of prime factors of value is lt 3
     *      but: #4 <5> was gt 3
     * primeFactorsOf(120) 
     *      prime factors of <120>
     * -- has(each(), lt(3))
     *      expected that prime factors of value has each lt 3
     *      but: had #4 <5> was gt 3
     * 
     * expected that each is less than 5
     * expected that each of list is less than 5
     * expected that each size primes any is 
     * that any of primes of size of each is 
     * that each of list prime-factors size is
     * expected that value is foo and size of prime-factors is
     * expected that each of list is foo and size of prime-factors is
     */
}
