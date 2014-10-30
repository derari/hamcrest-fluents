package org.cthul.matchers.fluent.gen.test;

import org.cthul.api4j.Api4JConfiguration;
import java.io.File;
import org.cthul.resolve.*;

public class TestConfiguration {
    
    public static File testOut() {
        return new File("target/test-out");
    }
    
    public static ResourceResolver testResolver() {
        File base = new File("src/test/api");
        ResourceResolver res = new CompositeResolver(
                new ClassResourceResolver(Api4JConfiguration.class).addDomain("", "/$1"),
                new FileResolver(base, base).addDomain(""));
        return res;
    }

    public static Api4JConfiguration get() {
        Api4JConfiguration g = new Api4JConfiguration(testOut(), testResolver());
        return g;
    }
    
    public static Api4JConfiguration addSource(Api4JConfiguration cfg) {
        cfg.getQdox().addSourceTree(new File("src/test/java"));
        return cfg;
    }
    
    public static Api4JConfiguration getWithSource() {
        return addSource(get());
    }
    
}
