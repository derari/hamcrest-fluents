package org.cthul.matchers.fluent.lib.object;

import org.junit.Test;

import static org.cthul.matchers.fluent.CoreFluents.assertThat;
import static org.hamcrest.Matchers.*;

/**
 *
 */
public class PropertyAdapterTest {
    
    public PropertyAdapterTest() {
    }
    
    @Test
    public void test_public_field() {
        PropertyAdapter<Integer> prop = PropertyAdapter.get("prop");
        assertThat(new OPublicField(3))
                .has(prop, equalTo(3));
    }
    
    @Test
    public void test_public_method() {
        PropertyAdapter<Integer> prop = PropertyAdapter.get("prop");
        assertThat(new OPublicMethod(3))
                .has(prop, equalTo(3));
    }
    
    @Test
    public void test_public_getter() {
        PropertyAdapter<Integer> prop = PropertyAdapter.get("prop");
        assertThat(new OPublicGetter(3))
                .has(prop, equalTo(3));
    }
    
    static class OPublicField {
        
        public int prop;

        public OPublicField(int prop) {
            this.prop = prop;
        }
        
        public int prop() {
            throw new UnsupportedOperationException();
        }
        
        public int getProp() {
            throw new UnsupportedOperationException();
        }
    }
    
    static class OPublicMethod {
        
        private int prop;

        public OPublicMethod(int prop) {
            this.prop = prop-1;
        }

        public int prop() {
            return prop+1;
        }
        
        public int getProp() {
            throw new UnsupportedOperationException();
        }
    }
    
    static class OPublicGetter {
        
        private int prop;

        public OPublicGetter(int prop) {
            this.prop = prop-1;
        }
        
        private int prop() {
            throw new UnsupportedOperationException();
        }

        public int getProp() {
            return prop+1;
        }
    }
}