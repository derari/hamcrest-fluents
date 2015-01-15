package org.cthul.matchers.fluent.gen.test;

import java.util.ArrayList;
import java.util.List;
import org.cthul.matchers.fluent.value.MatchValueAdapter;
import org.hamcrest.Factory;

public class Adapter1 {
    
    @Factory
    public static <Value, Property> A<Value,Property> getThing(List<Value> list, Class<Property> clazz) {
        return null;
    }
    
    @Factory
    public static B<Object,Double> getNumber() {
        return null;
    }
    
    @Factory
    public static MatchValueAdapter<Object, ArrayList<Double>> asList() {
        return null;
    }
    
    public static interface A<V,P> extends MatchValueAdapter<V,P> {
        
    }
    
    public static interface B<V,P extends Double> extends MatchValueAdapter<V,P> {
        
    }
}
