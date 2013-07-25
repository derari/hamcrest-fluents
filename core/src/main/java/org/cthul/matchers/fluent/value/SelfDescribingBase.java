package org.cthul.matchers.fluent.value;

import org.hamcrest.SelfDescribing;
import org.hamcrest.StringDescription;

/**
 *
 */
public abstract class SelfDescribingBase implements SelfDescribing {

    @Override
    public String toString() {
        StringDescription d = new StringDescription();
        describeTo(d);
        return d.toString();
    }
}
