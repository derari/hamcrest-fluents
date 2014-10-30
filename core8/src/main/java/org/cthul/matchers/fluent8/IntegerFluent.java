package org.cthul.matchers.fluent8;

import org.cthul.matchers.fluent.Fluent;
import org.hamcrest.Matcher;

/**
 *
 */
public interface IntegerFluent<Value, Property extends Number, ThisFluent extends Fluent<Value>, This extends IntegerFluent<Value, Property, ThisFluent, This>>
                    extends ObjectFluent<Value, Property, ThisFluent, This> {
    
    default ThisFluent greaterThan(Number n) {
        return __(X.greaterThan(n));
    }
    
    default ThisFluent lessThan(Number n) {
        return __(X.greaterThan(n));
    }
    
    static class X {
        static Matcher<Number> greaterThan(Number n) {
            return null;
        }
    }
}
