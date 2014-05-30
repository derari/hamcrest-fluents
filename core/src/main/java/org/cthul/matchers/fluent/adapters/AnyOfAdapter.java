package org.cthul.matchers.fluent.adapters;

import java.util.Arrays;
import org.cthul.matchers.fluent.value.MatchValue;
import org.hamcrest.Factory;

/**
 *
 * @param <Item>
 */
public class AnyOfAdapter<Item> extends 
                SimpleAnyOfAdapter<Iterable<? extends Item>, Item> {

    private static final AnyOfAdapter INSTANCE = new AnyOfAdapter<>();

    @Factory
    public static <I> AnyOfAdapter<I> anyOf() {
        return INSTANCE;
    }
    
    @Factory
    public static <I> MatchValue<I> anyOf(Iterable<? extends I> iterable) {
        return (MatchValue) INSTANCE.adapt(iterable);
    }
    
    @Factory
    public static <I> MatchValue<I> anyOf(I... array) {
        return anyOf(Arrays.asList(array));
    }
    
    @Factory
    public static <I> MatchValue<I> anyOf(MatchValue<? extends Iterable<? extends I>> mo) {
        return INSTANCE.adapt(mo);
    }
    
    @Factory
    public static <I> AnyOfAdapter<I> any() {
        return anyOf();
    }
    
    @Factory
    public static <I> MatchValue<I> any(Iterable<? extends I> iterable) {
        return anyOf(iterable);
    }
    
    @Factory
    public static <I> MatchValue<I> any(I... array) {
        return anyOf(Arrays.asList(array));
    }
    
    @Factory
    public static <I> MatchValue<I> any(MatchValue<? extends Iterable<? extends I>> value) {
        return anyOf(value);
    }
    
    @Factory
    public static <I> AnyOfAdapter<I> any(Class<I> c) {
        return any();
    }
    
    @Factory
    public static AnyOfAdapter<Object> anyItem() {
        return any();
    }
    
    @Factory
    public static AnyOfAdapter<String> anyString() {
        return any();
    }
    
    @Factory
    public static AnyOfAdapter<Byte> anyByte() {
        return any();
    }
    
    @Factory
    public static AnyOfAdapter<Boolean> anyBoolean() {
        return any();
    }
    
    @Factory
    public static AnyOfAdapter<Character> anyChar() {
        return any();
    }
    
    @Factory
    public static AnyOfAdapter<Double> anyDouble() {
        return any();
    }
    
    @Factory
    public static AnyOfAdapter<Float> anyFloat() {
        return any();
    }
    
    @Factory
    public static AnyOfAdapter<Integer> anyInt() {
        return any();
    }
    
    @Factory
    public static AnyOfAdapter<Long> anyLong() {
        return any();
    }
    
    @Factory
    public static AnyOfAdapter<Short> anyShort() {
        return any();
    }
    
    public AnyOfAdapter() {
        super(Iterable.class);
    }

    public AnyOfAdapter(String name) {
        super(name, Iterable.class);
    }
    
    public <T extends Item> MatchValue<T> of(Iterable<? extends T> value) {
        return (MatchValue) adapt(value);
    }

    @Override
    protected Iterable<? extends Item> getElements(Iterable<? extends Item> value) {
        return value;
    }
    
}
