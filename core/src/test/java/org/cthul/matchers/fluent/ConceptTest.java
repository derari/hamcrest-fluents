package org.cthul.matchers.fluent;

import java.util.*;
import org.cthul.matchers.fluent.adapters.SimpleAdapter;
import org.cthul.matchers.fluent.value.MatchValue;
import org.cthul.matchers.fluent.value.MatchValueAdapter;
import org.hamcrest.Matcher;
import org.junit.Test;
import static org.cthul.matchers.fluent.CoreFluents.*;
import static org.hamcrest.Matchers.*;

/**
 *
 */
public class ConceptTest {
    
    @Test
    public void dsl() {
        List<Integer> list = Arrays.asList(1, 3, 5);
        List<List<Integer>> metaList = Arrays.asList(list, Arrays.asList(1, 2, 3, 4));
        
        assertThat(1)
                .is(lessThan(3))
                .and(greaterThan(0));
        
        Matcher<Integer> negative_or_gt_10 = match(Integer.class)
                .is(lessThan(0))
                .or(greaterThan(10));
        assertThat(1).isNot(negative_or_gt_10);
        
        assertThat(eachOf(list)).is(lessThan(10));
        
        assertThat(list)
                .isNot(empty())
                .and(eachInt()).is(lessThan(10))
                .and(hasItem(1));
        
        Matcher<Iterable<? extends Integer>> each_is_gt_1 = match(eachInt())
                .is(greaterThan(1));
        assertThat(list).not(each_is_gt_1);
        
        assertThat(anyOf(list)).is(3);  
        
        assertThat(sizeOf(list)).is(3);  
        
        assertThat(list)
                .isNot(empty())
                .and(size()).is(lessThan(5))
                .and(eachInt()).is(lessThan(10))
                .and().not(hasItem(7));
        
        assertThat((Object) 1)
                .isA(Integer.class)
                .andIs(lessThan(3))
                .and(greaterThan(0));
        
        assertThat((Object) 1)
                .isA(Integer.class, lessThan(3))
                .and().isA(Integer.class, equalTo((Number) 1));
        
        assertThat(list)
                .isNot(empty())
                .and(each()).as(Integer.class).is(lessThan(10))
                .and(any()).isA(Integer.class, greaterThan(4))
                .and(hasItem(3));
        
        assertThat(eachOf(metaList))
                .has(size()).__(greaterThan(2));
        
        assertThat(sizeOf(eachOf(metaList)))
                .is(greaterThan(2));
        
        assertThat(anyOf(metaList).get(size()))
                .is(equalTo(3));
        
        assertThat(list)
                .has(size(), lessThan(10));
        
        Matcher<Object> empty_or_zero = match()
                .as(String.class).__(isEmptyString())
                .or().as(Integer.class).is(equalTo(0));
        assertThat(0)
                .is(empty_or_zero);
        assertThat("")
                .is(empty_or_zero);
        
        assertThat(sizeOf(), list)
                .is(lessThan(10));
        
        // assertThat(list, size())
        
        // assertThat(metaList, each(), size())
        //      .is(lessThan(10));
    }
    
    public static <T> MatchValue<Integer> sizeOf(MatchValue<? extends Collection<?>> value) {
        return size().adapt(value);
    }
    
    public static MatchValue<Integer> sizeOf(Collection<?> c) {
        return size().adapt(c);
    }
    
    public static MatchValueAdapter<Collection<?>, Integer> sizeOf() {
        return size();
    }
    
    public static MatchValueAdapter<Collection<?>, Integer> size() {
        return new SimpleAdapter<Collection<?>, Integer>("size") {
            @Override
            protected Integer adaptValue(Collection<?> v) {
                return v.size();
            }
        };
    }
}
