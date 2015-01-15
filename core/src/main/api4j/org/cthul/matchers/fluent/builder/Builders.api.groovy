api1 {

    def clFluentStep = "org.cthul.matchers.fluent.FluentStep".asClass()
    def clAbstractFluentBuilder = "org.cthul.matchers.fluent.builder.AbstractFluentBuilder".asClass()
    def clExtFluentAssert = "org.cthul.matchers.fluent.ext.ExtensibleFluentAssert".asClass()
    def clExtFluentMatcher = "org.cthul.matchers.fluent.ext.ExtensibleFluentMatcher".asClass()
    def clQuickDiagnosingMatcher = "org.cthul.matchers.diagnose.QuickDiagnosingMatcher".asClass()

    def isFluent = { m ->
        return m.returns.name == "TheFluent" || 
            (m.returns.name == "FluentStep" && m.typeParameters.empty)
    }

    def isStep = { m ->
        return m.returns.name == "FluentStep" && m.typeParameters.size() == 1
    }

    def fixMethod = { m, c -> 
        if (isFluent(m)) {
            m.tags.remove("return")
            m.tags << "return this"
            m.returns = c
        } else if (isStep(m)) {
            m.returns = clFluentStep.withArgs(m.typeParameters[0].name, "? extends " + c)
        } else {
            m.returns = m.returns.withArgs("Value", "? extends " + c)
        }
    }

    generatedClass ("AbstractFluentAssertBuilder") {
        tags << "param <Value> assertion value type"
        tags << "param <This> this type"
        modifiers << "abstract"
        typeParameters << ["Value", "This extends AbstractFluentAssertBuilder<Value, This>"]
        superClass = clAbstractFluentBuilder.withArgs("Value", "This")
        interfaces << clExtFluentAssert.withArgs("Value", "This")

        method("protected abstract void _and")

        method("public This and") {
            comment = "{@inheritDoc}"
            annotations << "Override"
            body << "_and();\nreturn _this();"
        }

        clFluentStep.methods.each {
            if ((it.name.contains("is") && it.name != "isA") 
                            || (it.name.contains("has") && it.name != "hasType")
                            || it.name.contains("not") || it.name.contains("__")) {
                def newName = "and" + (it.name == "__" ? "" : CamelCase(it.name));
                method(it, modifiers: "public") {
                    annotations << "Override"
                    name = newName
                    fixMethod(delegate, "This")
                    body << "_and();\n"
                    if (name.contains("Is")) body << "_is();\n"
                    if (name.contains("Has")) body << "_has();\n"
                    if (name.contains("Not")) body << "_not();\n"
                    if (parameters.empty) {
                        body << "return _this();"
                    } else if (parameters[0].type.name == "Value") {
                        body << "return equalTo(value);"
                    } else {
                        body << "return __(%s);" % argumentsString
                    }
                    comment = "{@inheritDoc}"
                    tags.clear()
                }
            }
        }
    }

    generatedClass ("AbstractFluentMatcherBuilder") {
        tags << "param <Value> fluent value type"
        tags << "param <Match> matcher element type"
        tags << "param <This> this type"
        modifiers << "abstract"
        typeParameters << ["Value", "Match", "This extends AbstractFluentMatcherBuilder<Value, Match, This>"]
        superClass = clAbstractFluentBuilder.withArgs("Value", "This")
        interfaces << clExtFluentMatcher.withArgs("Value", "Match", "This")

        method("protected abstract void _and")
        method("protected abstract void _or")

        method("public This and") {
            comment = "{@inheritDoc}"
            annotations << "Override"
            body << "_and();\nreturn _this();"
        }

        method("public This or") {
            comment = "{@inheritDoc}"
            annotations << "Override"
            body << "_or();\nreturn _this();"
        }

        clFluentStep.methods.each {
            if ((it.name.contains("is") && it.name != "isA") 
                            || (it.name.contains("has") && it.name != "hasType")
                            || it.name.contains("not") || it.name.contains("__")) {
                ["and", "or"].each { op ->
                    def newName = op + (it.name == "__" ? "" : CamelCase(it.name));
                    method(it, modifiers: "public") {
                        annotations << "Override"
                        name = newName
                        fixMethod(delegate, "This")
                        body << "_%s();\n" % op
                        if (name.contains("Is")) body << "_is();\n"
                        if (name.contains("Has")) body << "_has();\n"
                        if (name.contains("Not")) body << "_not();\n"
                        if (parameters.empty) {
                            body << "return _this();"
                        } else if (parameters[0].type.name == "Value") {
                            body << "return equalTo(value);"
                        } else {
                            body << "return __(%s);" % argumentsString
                        }
                        comment = "{@inheritDoc}"
                        tags.clear()
                    }
                }
            }
        }
    }
}