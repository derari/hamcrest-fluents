package org.cthul.matchers.fluent.ext;

import org.cthul.matchers.fluent.value.MatchValueAdapter;

/**
 * Combines a {@link MatchValueAdapter} with a {@link StepFactory step factory}.
 * 
 * @param <Value> value type
 * @param <NextValue> step value type
 * @param <TheFluent> fluent type
 * @param <BaseFluent> original fluent type for step
 * @param <TheStep> step type
 */
public interface ExtensionAdapter<Value, NextValue, TheFluent, BaseFluent, TheStep> {
    
    MatchValueAdapter<? super Value, ? extends NextValue> getAdapter();
    
    ExtensionFactory<NextValue, TheFluent, BaseFluent, TheStep> getFactory();
    
    ExtensionFactory<Value, TheFluent, BaseFluent, TheStep> asFactory();
    
    <BaseValue> ExtensionAdapter<BaseValue, NextValue, TheFluent, BaseFluent, TheStep> adaptTo(MatchValueAdapter<? super BaseValue, ? extends Value> adapter);
}
