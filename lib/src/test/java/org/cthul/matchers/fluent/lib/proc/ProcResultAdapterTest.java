package org.cthul.matchers.fluent.lib.proc;

import org.junit.Test;

import static org.cthul.matchers.fluent.CoreFluents.assertThat;
import static org.cthul.matchers.fluent.lib.proc.ProcResultAdapter.*;
import static org.cthul.proc.Procs.*;

/**
 *
 */
public class ProcResultAdapterTest {
    
    public static int sum(int... args) {
        int sum = 0;
        for (int i: args) {
            sum += i;
        }
        return sum;
    }
    
    @Test
    public void dsl() {
        assertThat(resultOf("sum", 1, 2, 3)).is(6);
    }
    
    @Test
    public void dsl2() {
        assertThat(invokeWith("sum", 4, 5, 6))
                .__(result()).is(15);
    }
}
