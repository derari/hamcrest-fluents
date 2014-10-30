package org.cthul.matchers.fluent8;

import org.cthul.matchers.fluent.Fluent;
import org.cthul.matchers.fluent.FluentProperty;
import org.hamcrest.Matcher;

/**
 *
 * @author C5173086
 */
public class Syntax {
    
    interface X<V, P, PF extends Fluent<V>, This extends X<V,P,PF,This>> extends FluentProperty<V,P> {
        
        @Override
        PF __(Matcher<? super P> matcher);
    }
    
}
