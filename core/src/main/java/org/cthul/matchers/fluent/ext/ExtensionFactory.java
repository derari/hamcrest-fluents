package org.cthul.matchers.fluent.ext;

import org.cthul.matchers.fluent.value.MatchValueAdapter;

/**
 * A factory for extensible fluents.
 * 
 * @param <Value> value type
 * @param <TheFluent> fluent type
 * @param <BaseFluent> original fluent type for step
 * @param <TheStep> step type
 */
public interface ExtensionFactory<Value, TheFluent, BaseFluent, TheStep> extends FluentFactory<Value, TheFluent>, StepFactory<Value, BaseFluent, TheStep> {
    
    FluentFactory<Value, TheFluent> getFluentFactory();
    
    StepFactory<Value, BaseFluent, TheStep> getStepFactory();
    
    <BaseValue> ExtensionAdapter<BaseValue, Value, TheFluent, BaseFluent, TheStep> adaptTo(MatchValueAdapter<? super BaseValue, ? extends Value> adapter);
}
