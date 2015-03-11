package org.cthul.matchers.fluent.gen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.cthul.matchers.fluent.gen.FluentsGenerator.AssertConfig;
import org.cthul.matchers.fluent.gen.FluentsGenerator.FactoryConfig;
import org.cthul.matchers.fluent.gen.FluentsGenerator.FluentConfig;
import org.cthul.matchers.fluent.gen.test.TestConfiguration;
import org.junit.Test;

public class FluentsGeneratorTest {
    
    @Test
    public void test_generate() throws Exception {
        FluentConfig flc;
        FactoryConfig fac;
        List<FluentConfig> configs = new ArrayList<>();
        flc = new FluentConfig("ObjectTestFluent", null);
        configs.add(flc);
        fac = new FactoryConfig("org.cthul.matchers.fluent.gen.test.Matcher1");
        fac.getIncludes().add("matcher1\\(");
        flc.getFactories().add(fac);
        fac = new FactoryConfig("org.cthul.matchers.fluent.gen.test.Adapter1");
        flc.getFactories().add(fac);
        
        flc = new FluentConfig("ListTestFluent", "java.util.List<?>");
        flc.getExtends().add("ObjectTestFluent");
        configs.add(flc);
        fac = new FactoryConfig("org.cthul.matchers.fluent.gen.test.Matcher2");
        fac.getIncludes().add("list");
        flc.getFactories().add(fac);
        
        AssertConfig asc = new AssertConfig("MyAssert");
        
        new FluentsGenerator(configs, Arrays.asList(asc)).generate(
                TestConfiguration.getWithSource(), 
                "test/t1");
    }
    
    
}
