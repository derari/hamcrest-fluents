api1 {

    def clFluentStep = "org.cthul.matchers.fluent.FluentStep".asClass()
    def clFluent = "org.cthul.matchers.fluent.Fluent".asClass()
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
    }

    generatedInterface ("FluentAssert") {
        comment = "A fluent that evaluates the matchers as they are added.\n" + 
                "What happens when a match fails is implementation specific,\n" +
                "an exception or error might be thrown."
        tags << "param <Value> assertion value type"
        typeParameters << "Value"
        interfaces << clFluent.withArgs("Value")

        method("and", returns: "FluentAssert<Value>") {
            comment = "Exists to improve the readability of fluent chains, does nothing."
            tags << "return this"
        }

        methods(clFluentStep.methods, see: false) {
            annotations << "Override"
            //if (name == "as") {
            //    comment = "Adds a matcher to the fluent that matches only instances of {@code clazz}."
            //    tags.remove("return")
            //    tags << "return this"
            //    returns = "FluentAssert<Value2>"
            //} else {}
            if (name == "hasType") {
                method("FluentAssert<Value2> isA(Class<Value2> clazz)") {
                    comment = "Checks that the value has the given type and changes the value type of this assertion"
                    tags << "param <Value2> expected value type"
                    tags << "param clazz expected value type"
                    tags << "return this"
                    typeParameters << "Value2 extends Value"
                }
            }
            fixMethod(delegate, "FluentAssert<Value>")    
            
            if ((name.contains("is") && name != "isA") 
                            || (name.contains("has") && name != "hasType")
                            || name.contains("not") || name.contains("__")) {
                def newName = "and" + (name == "__" ? "" : CamelCase(name));
                method(it, see: false) {
                    comment = "Equivalent to {@link #and() and()}{@link %s .%s()}" %
                                    [shortDocReference.replaceAll("\\bValue\\b", "Object"), name]
                    name = newName
                    fixMethod(delegate, "FluentAssert<Value>")
                }
            }
        }
    }

    generatedInterface ("FluentMatcher") {
        comment = "A fluent that builds a matcher.\n" + 
                "The matchers can be combined with one of {@link #and() and()}(default) or \n" + 
                "{@link #or() or()}. Attempting to change the\n" + 
                "chain type once it is set causes an {@link IllegalStateException}.\n" + 
                "<p>\n" + 
                "Once the fluent was used as a matcher, or {@link #getMatcher() getMatcher()}\n" + 
                "was called, it will be frozen.\n" + 
                "Any further modification causes an {@link IllegalStateException}."
        tags << "param <Value> fluent value type"
        tags << "param <Match> matcher element type"
        typeParameters << "Value"
        typeParameters << "Match"
        interfaces << clFluent.withArgs("Value")
        interfaces << clQuickDiagnosingMatcher.withArgs("Match")

        method("getMatcher", returns: clQuickDiagnosingMatcher.withArgs("Match")) {
            comment = "Freezes the fluent and builds a matcher."
            tags << "return matcher"
        }

        method("and", returns: "FluentMatcher<Value, Match>") {
            comment = "Defines this fluent to build a conjunction of matchers."
            tags << "return this"
            tags << "throws IllegalStateException if {@link #or() or()} was called before"
        }

        method("or", returns: "FluentMatcher<Value, Match>") {
            comment = "Defines this fluent to build a disjunction of matchers."
            tags << "return this"
            tags << "throws IllegalStateException if {@link #and() and()} was called before"
        }

        methods(clFluentStep.methods, see: false) {
            annotations << "Override"
            //if (name == "as") {
            //    comment = "Returns a fluent step that checks the value is an instance of {@code clazz} before applying a matcher."
            //    returns = clFluentStep.withArgs("Value2", "? extends FluentMatcher<Value, Match>")
            //} else { }
            fixMethod(delegate, "FluentMatcher<Value, Match>")
            
            if ((name.contains("is") && name != "isA") 
                            || (name.contains("has") && name != "hasType")
                            || name.contains("not") || name.contains("__")) {
                ["and", "or"].each { op ->
                    def newName = op + (name == "__" ? "" : CamelCase(name));
                    method(it, see: false) {
                        comment = "Equivalent to {@link #%s() %s()}{@link %s .s()}" %
                                        [op, op, shortDocReference.replaceAll("\\bValue\\b", "Object"), name]
                        name = newName
                        fixMethod(delegate, "FluentMatcher<Value, Match>")
                    }
                }
            }
        }
    }
}