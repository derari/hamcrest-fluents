package org.cthul.matchers.fluent.lib;

import java.util.*;
import org.cthul.matchers.fluent.adapters.SimpleAdapter;
import org.cthul.matchers.fluent.value.MatchValue;
import org.cthul.matchers.fluent.value.MatchValueAdapter;
import org.hamcrest.Matcher;
import org.junit.Test;
import static org.cthul.matchers.CoreFluents.match;
import static org.cthul.matchers.fluent.intern.CoreFluentsBase.*;
import static org.cthul.matchers.fluent.lib.MapValueAdapter.*;
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
                ._(intValue("one")).is(lessThan(3))
                .and(value("two")).isA(Integer.class, lessThan(3));
        
    }
}
