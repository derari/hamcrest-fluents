package org.cthul.matchers.fluent.value;

/**
 *
 */
public abstract class MatchValueBase<Value> 
                extends SelfDescribingBase
                implements MatchValue<Value> {

    @Override
    public <Property> MatchValue<Property> get(MatchValueAdapter<? super Value, Property> adapter) {
        return adapter.adapt(this);
    }

}
