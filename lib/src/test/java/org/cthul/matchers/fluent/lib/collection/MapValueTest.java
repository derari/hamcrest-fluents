package org.cthul.matchers.fluent.lib.collection;

import java.util.*;
import org.junit.Test;

import static org.cthul.matchers.fluent.CoreFluents.assertThat;
import static org.cthul.matchers.fluent.lib.collection.MapAdapters.*;
import static org.hamcrest.Matchers.*;

/**
 *
 */
public class MapValueTest {
    
    @Test
    public void dsl() {
        Map<String, Integer> map = new HashMap<String, Integer>(){{
           put("one", 1); put("two", 2); put("three", 3);
        }};
        assertThat(map)
                .__(intValue("one")).is(lessThan(3))
                .and(value("two")).isA(Integer.class, lessThan(3));
        
    }
}
