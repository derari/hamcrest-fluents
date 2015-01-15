package org.cthul.matchers.fluent.builder;

import org.cthul.matchers.fluent.Fluent;
import org.cthul.matchers.fluent.ext.ExtensibleFluent;

/**
 * Base class for all fluent implementations.
 * @param <Value> value type
 * @param <This> this type
 */
public abstract class AbstractFluentBuilder<Value, This extends AbstractFluentBuilder<Value, This>>
                extends AbstractFluentStepBuilder<Value, Fluent<Value>, This, This>
                implements ExtensibleFluent<Value, This> {

}
