package org.cthul.matchers.fluent.ext;

import org.cthul.matchers.fluent.FluentStep;
import org.cthul.matchers.fluent.adapters.AsTypeAdapter;
import org.cthul.matchers.fluent.value.MatchValueAdapter;

/**
 * Combines a {@link MatchValueAdapter} with a {@link NewStep step factory}.
 * 
 * @param <Value> value type
 * @param <NextValue> step value type
 * @param <TheFluent> original fluent type
 * @param <TheStep> step type
 */
public interface ExtensibleStepAdapter<Value, NextValue, TheFluent, TheStep> {
    
    MatchValueAdapter<Value, NextValue> getAdapter();
    
    NewStep<NextValue, TheFluent, TheStep> getStepFactory();
    
    class New {
        public static <V, Fl, S> ExtensibleStepAdapter<Object, V, Fl, S> forType(Class<V> clazz, final NewStep<V, Fl, S> newStep) {
            final MatchValueAdapter<Object, V> typeAdapter = AsTypeAdapter.as(clazz);
            return new ExtensibleStepAdapter<Object, V, Fl, S>() {
                @Override
                public MatchValueAdapter<Object, V> getAdapter() {
                    return typeAdapter;
                }
                @Override
                public NewStep<V, Fl, S> getStepFactory() {
                    return newStep;
                }
            };
        }
        
        public static <T> T __adapt(FluentStep<?,?> step, MatchValueAdapter<?,?> adapter0, ExtensibleStepAdapter<?,?,?,?> adapter1) {
            return __adapt(step.__((MatchValueAdapter) adapter0), adapter1);
        }
        
        public static <T> T __adapt(FluentStep<?,?> step, ExtensibleStepAdapter<?,?,?,?> adapter) {
            return (T) ((ExtensibleFluentStep) step).as(adapter);
        }
    }
}
