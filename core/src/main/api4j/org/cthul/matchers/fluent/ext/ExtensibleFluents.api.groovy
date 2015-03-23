api1 {

    def clFluentStep = "org.cthul.matchers.fluent.FluentStep".asClass()
    def clFluent = "org.cthul.matchers.fluent.Fluent".asClass()
    def clFluentAssert = "org.cthul.matchers.fluent.FluentAssert".asClass()
    def clFluentMatcher = "org.cthul.matchers.fluent.FluentMatcher".asClass()
    def clQuickDiagnosingMatcher = "org.cthul.matchers.diagnose.QuickDiagnosingMatcher".asClass()

    def isFluent = { m ->
        return m.returns.name == "TheFluent" || 
            (m.returns.name == "FluentStep" && m.typeParameters.empty)
    }

    def isStep = { m ->
        return m.returns.name == "FluentStep" && m.typeParameters.size() == 1
    }

    def isChain = { m ->
        return m.returns.name.contains("Chain")
    }

    def fixMethod = { m, c -> 
        if (isFluent(m)) {
            m.tags.remove("return")
            m.tags << "return this"
            m.returns = c
        } else if (isStep(m)) {
            m.returns = clFluentStep.withArgs(m.typeParameters[0].name, "? extends " + c)
        } else if (isChain(m)) {
            m.returns = m.returns.withArgs("Value", "? extends " + c, "?")
        } else {
            m.returns = m.returns.withArgs("Value", "? extends " + c)
        }
        if (m.comment) {
            m.comment = m.comment.replace("{@link Fl", "{@link org.cthul.matchers.fluent.Fl")
        }
    }

    generatedInterface ("ExtensibleFluentStep") {
        comment = "Extensible version of {@link %s FluentStep}." % clFluentStep.fullyQualifiedName
        tags << "param <Value> value type"
        tags << "param <BaseFluent> second argument to {@link %s FluentStep}" % clFluentStep.fullyQualifiedName
        tags << "param <TheFluent> original fluent type"
        tags << "param <This> this type"
        typeParameters << ["Value", "BaseFluent", "TheFluent extends BaseFluent", 
                "This extends ExtensibleFluentStep<Value, BaseFluent, TheFluent, This>"]
        interfaces << clFluentStep.withArgs("Value", "BaseFluent")

        def stepMethods = clFluentStep.methods.grep { it.returns.name == "FluentStep" && it.typeParameters.empty }
        methods(stepMethods, see: false) {
            annotations << "Override"
            returns = "This"
        }

        def otherMethods = clFluentStep.methods.grep { !stepMethods.contains(it) }
        methods(otherMethods, see: false) {
            annotations << "Override"
            if (comment) {
                comment = comment.replace("{@link Fl", "{@link org.cthul.matchers.fluent.Fl")
            }
        }

        method("Step as(StepFactory<? super Value, TheFluent, Step> adapter)") {
            typeParameters << "Step"
            comment = "Returns a fluent step that adapts the value before applying a matcher."
            tags << "param <Step> step type"
            tags << "param adapter extensible step adapter"
            tags << "return step"
        }
    }

    generatedInterface ("ExtensibleFluent") {
        typeParameters << ["Value", 
                "This extends ExtensibleFluent<Value, This>"]
        interfaces << clFluent.withArgs("Value")
        interfaces << "ExtensibleFluentStep".asClass().withArgs("Value", clFluent.withArgs("Value").name, "This", "This")

        methods(clFluentStep.methods, see: false) {
            annotations << "Override"
            //if (name == "as") {
            //    returns = clFluentStep.withArgs("Value2", "? extends " + clFluent.withArgs("? extends Value").name)
            //} else { }
            fixMethod(delegate, "This")
        }
    }

    generatedInterface ("ExtensibleFluentAssert") {
        typeParameters << ["Value", 
                "This extends ExtensibleFluentAssert<Value, This>"]
        interfaces << clFluentAssert.withArgs("Value")
        interfaces << "ExtensibleFluent".asClass().withArgs("Value", "This")

        methods(clFluentStep.methods, see: false) {
            annotations << "Override"
            //if (name == "as") {
            //    comment = "Adds a matcher to the fluent that matches only instances of {@code clazz}."
            //    tags.remove("return")
            //    tags << "return this"
            //    returns = clFluentAssert.withArgs("Value2")
            //} else {
            fixMethod(delegate, "This")

            if ((name.contains("is") && name != "isA") 
                            || (name.contains("has") && name != "hasType")
                            || name.contains("not") || name.contains("__")) {
                def newName = "and" + (name == "__" ? "" : CamelCase(name));
                method(it, see: false) {
                    annotations << "Override"
                    comment = "Equivalent to {@link #and() and()}{@link %s .%s()}" %
                                    [shortDocReference.replaceAll("\\bValue\\b", "Object"), name]
                    name = newName
                    fixMethod(delegate, "This")
                }
            }
        }
    }

    generatedInterface ("ExtensibleFluentMatcher") {
        interfaces << clFluentMatcher.withArgs("Value", "Match")
        interfaces << "ExtensibleFluent".asClass().withArgs("Value", "This")
        typeParameters << ["Value", "Match", 
                "This extends ExtensibleFluentMatcher<Value, Match, This>"]

        method("getMatcher", returns: clQuickDiagnosingMatcher.withArgs("Match")) {
            comment = "Freezes the fluent and builds a matcher."
            tags << "return matcher"
        }

        method("and", returns: "This") {
            annotations << "Override"
            comment = "Defines this fluent to build a conjunction of matchers."
            tags << "return this"
            tags << "throws IllegalStateException if {@link #or() or()} was called before"
        }

        method("or", returns: "This") {
            annotations << "Override"
            comment = "Defines this fluent to build a disjunction of matchers."
            tags << "return this"
            tags << "throws IllegalStateException if {@link #and() and()} was called before"
        }

        methods(clFluentStep.methods, see: false) {
            annotations << "Override"
            //if (name == "as") {
            //    comment = "Returns a fluent step that checks the value is an instance of {@code clazz} before applying a matcher."
            //    returns = clFluentStep.withArgs("Value2", "This")
            //} else {
            fixMethod(delegate, "This")
            
            if ((name.contains("is") && name != "isA") 
                            || (name.contains("has") && name != "hasType")
                            || name.contains("not") || name.contains("__")) {
                ["and", "or"].each { op ->
                    def newName = op + (name == "__" ? "" : CamelCase(name));
                    method(it, see: false) {
                        comment = "Equivalent to {@link #%s() %s()}{@link %s .%s()}" %
                                        [op, op, shortDocReference.replaceAll("\\bValue\\b", "Object"), name]
                        name = newName
                        fixMethod(delegate, "This")
                    }
                }
            }
        }
    }
}