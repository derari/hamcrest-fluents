package org.cthul.matchers.fluent8;

import java.util.function.BiFunction;
import org.cthul.matchers.fluent.adapters.IdentityValue;
import org.cthul.matchers.fluent.builder.FailureHandler;
import org.cthul.matchers.fluent.value.MatchValue;

/**
 *
 */
public class FluentTools {
    
    public static <Value, Fluent> Fluent assertThat(MatchValue<Value> value, BiFunction<FailureHandler, MatchValue<Value>, Fluent> factory) {
        FailureHandler fh = null;
        return factory.apply(fh, value);
    }
    
    public static ActualFluentAssert assertThat(Object o) {
        return assertThat(IdentityValue.value(o), ActualFluentAssert::new);
    }
    
    static {
        assertThat("").is().nullValue().notNullValue();
    }
    
}
