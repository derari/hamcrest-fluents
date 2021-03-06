package org.cthul.matchers.fluent.adapters;

import java.util.Arrays;
import org.cthul.matchers.fluent.value.MatchValue;
import org.hamcrest.Factory;

/**
 *
 * @param <Item>
 */
public class EachOfAdapter<Item> extends 
                SimpleEachOfAdapter<Iterable<? extends Item>, Item> {

    private static final EachOfAdapter INSTANCE = new EachOfAdapter<>();

    @Factory
    public static <I> EachOfAdapter<I> eachOf() {
        return INSTANCE;
    }
    
    @Factory
    public static <I> MatchValue<I> eachOf(Iterable<? extends I> iterable) {
        return (MatchValue) INSTANCE.adapt(iterable);
    }
    
    @Factory
    public static <I> MatchValue<I> eachOf(I... array) {
        return eachOf(Arrays.asList(array));
    }
    
    @Factory
    public static <I> MatchValue<I> eachOf(MatchValue<? extends Iterable<? extends I>> mo) {
        return INSTANCE.adapt(mo);
    }
    
    @Factory
    public static <I> EachOfAdapter<I> each() {
        return INSTANCE;
    }
    
    @Factory
    public static <I> EachOfAdapter<I> each(Class<I> c) {
        return each();
    }
    
    @Factory
    public static <I> MatchValue<I> each(Iterable<? extends I> iterable) {
        return eachOf(iterable);
    }
    
    @Factory
    public static <I> MatchValue<I> each(I... array) {
        return eachOf(Arrays.asList(array));
    }
    
    @Factory
    public static <I> MatchValue<I> each(MatchValue<? extends Iterable<? extends I>> mo) {
        return eachOf(mo);
    }
    
    @Factory
    public static EachOfAdapter<Object> eachItem() {
        return each();
    }
    
    @Factory
    public static EachOfAdapter<String> eachString() {
        return each();
    }
    
    @Factory
    public static EachOfAdapter<Byte> eachByte() {
        return each();
    }

    @Factory
    public static EachOfAdapter<Boolean> eachBoolean() {
        return each();
    }

    @Factory
    public static EachOfAdapter<Character> eachChar() {
        return each();
    }

    @Factory
    public static EachOfAdapter<Double> eachDouble() {
        return each();
    }

    @Factory
    public static EachOfAdapter<Float> eachFloat() {
        return each();
    }

    @Factory
    public static EachOfAdapter<Integer> eachInt() {
        return each();
    }

    @Factory
    public static EachOfAdapter<Long> eachLong() {
        return each();
    }

    @Factory
    public static EachOfAdapter<Short> eachShort() {
        return each();
    }

    public EachOfAdapter() {
    }

    public EachOfAdapter(String name) {
        super(name);
    }

    @Override
    protected Iterable<? extends Item> getElements(Iterable<? extends Item> value) {
        return value;
    }
    
}
