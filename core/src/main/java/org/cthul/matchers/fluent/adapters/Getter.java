package org.cthul.matchers.fluent.adapters;

public interface Getter<Value, Property> {

    Property get(Value value);
}
