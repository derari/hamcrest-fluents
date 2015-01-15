package org.cthul.matchers.fluent8;

import java.util.List;
import org.cthul.matchers.fluent.Fluent;
import org.cthul.matchers.fluent.FluentStep;
import org.cthul.matchers.fluent.adapters.AsTypeAdapter;
import org.cthul.matchers.fluent.adapters.EachOfAdapter;
import org.cthul.matchers.fluent.builder.FailureHandler;
import org.cthul.matchers.fluent.builder.FluentAssertBuilder;
import org.cthul.matchers.fluent.ext.ExtensibleFluent;
import org.cthul.matchers.fluent.ext.ExtensibleFluentStep;
import org.cthul.matchers.fluent.builder.Matchable;
import org.cthul.matchers.fluent.ext.ExtensibleStepAdapter;
import org.cthul.matchers.fluent.ext.NewStep;
import org.cthul.matchers.fluent.value.MatchValue;
import org.cthul.matchers.fluent.value.MatchValueAdapter;
import org.hamcrest.Matcher;

/**
 *
 */
public class TestAssert {
    
    public static DoubleAssert assertThat(double value) {
        return null;
    }
    
    public static DoubleAssert assertDouble(MatchValue<? extends Double> value) {
        return null;
    }

    static {
        List<Double> list = null;
        assertDouble(EachOfAdapter.eachOf(list))
                .is().closeTo(1d, 0.001d);
    }
//    
//    public static <Fl extends DoubleFluent.Step<Double, ?, Fl>> NewStep<Double, Fl> aDouble() {
//        //return m -> (Fl) new DoubleFluent.Step(m);
////        return new NewStep<Double, TheFluent, DoubleFluent.Step<Double, TheFluent, ?>>() {
////            @Override
////            public DoubleFluent.Step<Double, TheFluent, ?> create(Matchable<Double, TheFluent> matchable) {
////                return (DoubleFluent.Step) new DoubleFluent.Step<>(matchable);
////            }
////        };
//        return null;
//    }
    
    static {
         //TestAssert.<DoubleFluent.Step<Double, ?, ?>>
//        MyFluent<Number, String> fl = null;
//        fl.as(extDouble()).lessThan(3.0).substring(1);
        ObjectAssert oa = null;
        oa.as(aDouble()).is().lessThan(3.0).hasToString("");
//        oa._is(aDouble2());
        oa.isADouble().lessThan(3.0).greaterThan(0.0);
        //oa.as(ComparableFluent.Step.adapter()).lessThan(3.0).hasToString("");
        oa.as(DoubleFluent.Step.adapter()).lessThan(3.0).hasToString("");
        
        ComparableFluent<Double, Double, ObjectAssert, ObjectAssert, ?> fl;// = oa.as(ComparableFluent.Step.adapter());
    }
    
    static <Fl> ExtensibleStepAdapter<Object, Double, Fl, DoubleFluent<Double,Fl,Fl,?>> aDouble() {
        return null;
    }
    
    static <Fl extends DoubleFluent.Step<Double,Fl,Fl>> ExtensibleStepAdapter<Object, Double, Fl, Fl> aDouble2() {
        return null;
    }
    
    public static class ObjectAssert 
                    extends FluentAssertBuilder<Object, ObjectAssert>
                    implements ObjectFluent<Object, Fluent<Object>, ObjectAssert, ObjectAssert> {

        public ObjectAssert(FailureHandler failureHandler, MatchValue<Object> matchValues) {
            super(failureHandler, matchValues);
        }

        @Override
        public ObjectFluent.OrChain<Object, ObjectAssert, ?> either() {
            return (ObjectFluent.OrChain) super.either();
        }

        @Override
        public ObjectFluent.OrChain<Object, ObjectAssert, ?> either(Matcher<? super Object> matcher) {
            return (ObjectFluent.OrChain) super.either(matcher);
        }
        
        public DoubleAssert isADouble() {
            return null;
        }
    }
    
    public static class DoubleAssert 
                    extends FluentAssertBuilder<Double, DoubleAssert>
                    implements DoubleFluent<Double, Fluent<Double>, DoubleAssert, DoubleAssert> {

        public DoubleAssert(FailureHandler failureHandler, MatchValue<Double> matchValues) {
            super(failureHandler, matchValues);
        }

        @Override
        public DoubleFluent.OrChain<Double, DoubleAssert, ?> either() {
            return (DoubleFluent.OrChain) super.either();
        }

        @Override
        public DoubleFluent.OrChain<Double, DoubleAssert, ?> either(Matcher<? super Double> matcher) {
            return (DoubleFluent.OrChain) super.either(matcher);
        }
    }
}
