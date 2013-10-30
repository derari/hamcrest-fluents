package org.cthul.matchers.fluent.lib;

import java.lang.reflect.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.cthul.matchers.fluent.adapters.SimpleAdapter;
import org.hamcrest.Description;
import org.hamcrest.Factory;

/**
 *
 */
public class PropertyAdapter<Property> extends SimpleAdapter<Object, Property> {
    
    @Factory
    public static <P> PropertyAdapter<P> property(String name) {
        return new PropertyAdapter<>(name);
    }
    
    @Factory
    public static <P> PropertyAdapter<P> property(String name, Class<P> clazz) {
        return new PropertyAdapter<>(name);
    }
    
    @Factory
    public static <P> PropertyAdapter<P> get(String name) {
        return property(name);
    }
    
    @Factory
    public static <P> PropertyAdapter<P> get(String name, Class<P> clazz) {
        return property(name);
    }
    
    @Factory
    public static PropertyAdapter<Boolean> getBoolean(String name) {
        return property(name);
    }
    
    @Factory
    public static PropertyAdapter<Integer> getInt(String name) {
        return property(name);
    }
    
    private final String name;
    private Class valueType = null;
    private Object property = null;
    private ConcurrentMap<Class, Object> properties = null;

    public PropertyAdapter(String name) {
        super(name, Object.class);
        this.name = name;
    }
    
    private Object getProperty(Class clazz) {
        if (valueType == clazz && property != null) {
            return property;
        }
        if (properties != null) {
            Object p = properties.get(clazz);
            if (p != null) return p;
        }
        Object p = findProperty(clazz);
        storeProperty(clazz, p);
        return p;
    }
    
    private Object findProperty(Class clazz) {
        try {
            return clazz.getField(name);
        } catch (NoSuchFieldException e) { }
        try {
            return clazz.getMethod(name);
        } catch (NoSuchMethodException e) { }
        try {
            String s = "get" +
                    name.substring(0, 1).toUpperCase() +
                    (name.length() == 1 ? "" : name.substring(1));
            return clazz.getMethod(s);
        } catch (NoSuchMethodException e) { }        
        return Void.class;
    }

    private void storeProperty(Class clazz, Object p) {
        if (valueType == null) {
            synchronized (this) {
                if (valueType == null) {
                    property = p;
                    valueType = clazz;
                    return;
                }
            }
        }
        if (properties == null) {
            ConcurrentMap<Class, Object> map = new ConcurrentHashMap<>();
            synchronized (this) {
                if (properties == null) {
                    properties = map;
                }
            }
        }
        properties.put(clazz, p);
    }

    @Override
    protected boolean acceptValue(Object value) {
        return getProperty(value.getClass()) != null;
    }

    @Override
    protected void describeExpectedToAccept(Object value, Description description) {
        description.appendText("a value with property '")
                .appendText(name).appendText("'");
    }

    @Override
    protected void describeMismatchOfUnaccapted(Object value, Description description) {
        description.appendValue(value)
                .appendText(" had no property '")
                .appendText(name).appendText("'");
    }

    @Override
    protected Property adaptValue(Object v) {
        Object prop = getProperty(v.getClass());
        Object p;
        try {
            if (prop instanceof Field) {
                p = ((Field) prop).get(v);
            } else if (prop instanceof Method) {
                p = ((Method) prop).invoke(v);
            } else {
                throw new RuntimeException("Internal error: " +
                        v + " has no property '" + name + "'");
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return (Property) p;
    }
}
