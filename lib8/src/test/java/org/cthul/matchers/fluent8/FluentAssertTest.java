package org.cthul.matchers.fluent8;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import static org.cthul.matchers.fluent8.FluentAssert.*;

public class FluentAssertTest {
    
    @Test
    public void test_assertInteger() {
        assertThat(5)
                .is().lessThan(6)
                .and().greaterThan(2);
    }
    
    @Test
    public void test_assertList() {
        List<Integer> list = Arrays.asList(1, 2, 3);
        assertThat(list)
                .contains(1, 2, 3)
                .containsInAnyOrder(3, 2, 1)
                .has().eachInt().lessThan(53);
    }
    
}
