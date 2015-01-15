package org.cthul.matchers;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import static org.cthul.matchers.Fluents.*;
import static org.cthul.matchers.FlMatchers.*;

public class FluentsTest {
    
    @Test
    public void test_compiled() {
        List<Object> list = Arrays.asList((Object) 1, 2, 3);
        assertThat(eachOf(list))
                .hasType(Integer.class)
                .and().is(lessThan(10));
    }
}
