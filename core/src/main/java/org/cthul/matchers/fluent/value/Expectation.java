package org.cthul.matchers.fluent.value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import org.hamcrest.Description;
import org.hamcrest.SelfDescribing;
import org.hamcrest.StringDescription;

/**
 *
 */
public class Expectation 
                extends SelfDescribingBase
                implements MatchValue.ExpectationDescription {
    
    private ArrayList<SelfDescribing> sorted = null;
    private ArrayList<SelfDescribing> unsorted = null;
    private StringDescription current = null;

    protected StringDescription current() {
        if (current == null) {
            current = new StringDescription();
        }
        return current;
    }

    @Override
    public void addExpected(int index, SelfDescribing expected) {
        if (index < 0) {
            if (unsorted == null) unsorted = new ArrayList<>();
            unsorted.add(expected);
        } else {
            if (sorted == null) sorted = new ArrayList<>();
            sorted.ensureCapacity(index);
            while (sorted.size() <= index) sorted.add(null);
            sorted.set(index, expected);
        }
        current = null;
    }

    @Override
    public Description appendText(String text) {
        return current().appendText(text);
    }

    @Override
    public Description appendDescriptionOf(SelfDescribing value) {
        return current().appendDescriptionOf(value);
    }

    @Override
    public Description appendValue(Object value) {
        return current().appendValue(value);
    }

    @Override
    public <T> Description appendValueList(String start, String separator, String end, T... values) {
        return current().appendValueList(start, separator, end, values);
    }

    @Override
    public <T> Description appendValueList(String start, String separator, String end, Iterable<T> values) {
        return current().appendValueList(start, separator, end, values);
    }

    @Override
    public Description appendList(String start, String separator, String end, Iterable<? extends SelfDescribing> values) {
        return current().appendList(start, separator, end, values);
    }

    @Override
    public void describeTo(Description description) {
        if (current != null) {
            description.appendText(current.toString());
            description.appendText(" ");
        }
        
        LinkedHashSet<String> expected = new LinkedHashSet<>();
        describeAll(expected, unsorted);
        describeAll(expected, sorted);
        
        final int last = expected.size() - 1;
        int i = 0;
        for (String s : expected) {
            if (i > 0) {
                if (i == last) {
                    description.appendText(", and ");
                } else {
                    description.appendText(", ");
                }
            }
            description.appendText(s);
            i++;
        }
    }

    private void describeAll(Collection<String> target, Iterable<SelfDescribing> source) {
        if (source != null) {
            for (SelfDescribing s: source) {
                if (s != null) {
                    StringDescription sd = new StringDescription();
                    sd.appendDescriptionOf(s);
                    target.add(sd.toString());
                }
            }
        }
    }
    
}
