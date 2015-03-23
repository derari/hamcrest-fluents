package org.cthul.matchers.fluent8;

import java.util.Arrays;
import java.util.List;
import org.cthul.matchers.fluent.ext.ExtensionAdapter;
import org.cthul.matchers.fluent.ext.ExtensionFactory;
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
        List<Integer> list = Arrays.asList(3, 2, 1);
        assertThat(list)
                .isNot().empty()
                .and().either()
                        .contains(4, 5, 6)
                        .or().containsInAnyOrder(1, 2, 3)
                .andHas().eachInt().lessThan(10)
                .and().hasSize(3);
    }
    
    @Test
    public void test_cast() {
        assertThat((Object) 5)
                .asInteger()
                .is().lessThan(7)
                .and().greaterThanOrEqualTo(5);
    }    
}
