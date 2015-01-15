package org.cthul.matchers.fluent.adapters;

import org.hamcrest.Factory;
import org.hamcrest.internal.ReflectiveTypeFinder;

public class LambdaAdapter<Value, Property> extends SimpleAdapter<Value, Property> {
    
    @Factory
    public static <Value, Property> LambdaAdapter<Value, Property> get(String name, Getter<Value, Property> getter) {
        return new LambdaAdapter<>(name, getter);
    }
    
    @Factory
    public static <Value, Property> LambdaAdapter<Value, Property> get(Getter<Value, Property> getter) {
        return new LambdaAdapter<>(getter);
    }
    
    @Factory
    public static <Value, Property> LambdaAdapter<Value, Property> adapt(String name, Getter<Value, Property> getter) {
        return new LambdaAdapter<>(name, getter);
    }
    
    @Factory
    public static <Value, Property> LambdaAdapter<Value, Property> adapt(Getter<Value, Property> getter) {
        return new LambdaAdapter<>(getter);
    }
    
    private static final ReflectiveTypeFinder GETTER_TYPE = new ReflectiveTypeFinder("get", 1, 0);
    
    private final Getter<Value, Property> getter;

    public LambdaAdapter(String name, Getter<Value, Property> getter) {
        super(name, (Class) GETTER_TYPE.findExpectedType(getter.getClass()));
        this.getter = getter;
    }
    
    public LambdaAdapter(Getter<Value, Property> getter) {
        super(null, (Class) GETTER_TYPE.findExpectedType(getter.getClass()));
        this.getter = getter;
    }

    @Override
    protected Property adaptValue(Value v) {
        return getter.get(v);
    }
    
}
