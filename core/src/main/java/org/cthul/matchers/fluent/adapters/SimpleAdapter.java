package org.cthul.matchers.fluent.adapters;

import org.hamcrest.Description;
import org.hamcrest.internal.ReflectiveTypeFinder;

/**
 * A value adapter intended for easy subclassing.
 * Only one method has to be implemented, and a name should be provided
 * to the constructor.
 */
public abstract class SimpleAdapter<Value, Item> extends ConvertingAdapter<Value, Item> {
    
    private final String name;

    public SimpleAdapter(String name) {
        this.name = name;
    }

    public SimpleAdapter(String name, Class<Value> valueType) {
        super(valueType);
        this.name = name;
    }

    protected SimpleAdapter(String name, ReflectiveTypeFinder typeFinder) {
        super(typeFinder);
        this.name = name;
    }

    @Override
    protected boolean hasDescription() {
        return name != null;
    }

    @Override
    public void describeTo(Description description) {
        if (name != null) {
            description.appendText(name);
            return;
        }
        Class<?> c = getClass();
        String n = c.getSimpleName();
        if (!n.isEmpty()) {
            description.appendText(n);
            return;
        }
        description.appendText(c.getName())
                   .appendText("@")
                   .appendText(String.valueOf(hashCode()));
    }
}
