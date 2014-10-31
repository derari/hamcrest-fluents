package org.cthul.matchers.fluent.builder;

import org.cthul.matchers.fluent.Fluent;
import org.cthul.matchers.fluent.ext.ExtendableFluent;

/**
 *
 */
public abstract class AbstractFluentBuilder<Value, This extends AbstractFluentBuilder<Value, This>>
                extends AbstractFluentPropertyBuilder<Value, Fluent<? extends Value>, This, This>
                implements ExtendableFluent<Value, This> {

}
