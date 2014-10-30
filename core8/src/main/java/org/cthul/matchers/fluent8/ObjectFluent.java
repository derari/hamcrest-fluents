package org.cthul.matchers.fluent8;

import org.cthul.matchers.fluent.Fluent;
import org.cthul.matchers.fluent.ext.ExtendableFluentProperty;
import org.hamcrest.core.IsNull;
import org.hamcrest.core.IsSame;

public interface ObjectFluent<Value, Property, ThisFluent extends Fluent<Value>, This extends ObjectFluent<Value, Property, ThisFluent, This>> 
    extends ExtendableFluentProperty<Value, Property, ThisFluent, This> {
    
    default ThisFluent sameInstance(Property same) {
        return __(IsSame.sameInstance(same));
    }
    
    default ThisFluent nullValue() {
        return __(IsNull.nullValue());
    }
    
    default ThisFluent notNullValue() {
        return __(IsNull.notNullValue());
    }
}
