api1 {

    def clFluentProperty = "org.cthul.matchers.fluent.FluentProperty".asClass()
    def clFluent = "org.cthul.matchers.fluent.Fluent".asClass()
    def clFluentAssert = "org.cthul.matchers.fluent.FluentAssert".asClass()
    def clFluentMatcher = "org.cthul.matchers.fluent.FluentMatcher".asClass()

    generatedInterface ("ExtendableFluentProperty") {
        interfaces << clFluentProperty.withArgs("Property", "ThisFluent")
        typeParameters << ["Property", "ThisFluent", 
                "This extends ExtendableFluentProperty<Property, ThisFluent, This>"]

        def propMethods = clFluentProperty.methods.grep { it.returns.name.endsWith("Property") && it.typeParameters.empty }
        methods(propMethods) {
            annotations << "Override"
            returns = "This"
            tags.remove(tags.size()-1)
        }
    }

    generatedInterface ("ExtendableFluent") {
        interfaces << clFluent.withArgs("Value")
        interfaces << "ExtendableFluentProperty".asClass().withArgs("Value", "org.cthul.matchers.fluent.Fluent<? extends Value>", "This")
        typeParameters << ["Value", 
                "This extends ExtendableFluent<Value, This>"]

        def fluentMethods = clFluent.methods.grep { it.returns.name.endsWith("Fluent") }
        methods(fluentMethods) {
            annotations << "Override"
            returns = "This"
            tags.remove(tags.size()-1)
        }

        def propertyMethods = clFluent.methods.grep { it.returns.name.endsWith("Property") && it.name != "as" }
        methods(propertyMethods) {
            annotations << "Override"
            returns = clFluentProperty.withArgs(typeParameters[0].name, "This")
            tags.remove(tags.size()-1)
        }
    }

    generatedInterface ("ExtendableFluentAssert") {
        interfaces << clFluentAssert.withArgs("Value")
        interfaces << "ExtendableFluent".asClass().withArgs("Value", "This")
        typeParameters << ["Value", 
                "This extends ExtendableFluentAssert<Value, This>"]

        def fluentMethods = clFluentAssert.methods.grep { it.returns.name.endsWith("FluentAssert") && it.name != "as" }
        methods(fluentMethods) {
            annotations << "Override"
            returns = "This"
            tags.remove(tags.size()-1)
        }

        def propertyMethods = clFluentAssert.methods.grep { it.returns.name.endsWith("Property") }
        methods(propertyMethods) {
            annotations << "Override"
            returns = clFluentProperty.withArgs(typeParameters[0].name, "This")
            tags.remove(tags.size()-1)
        }
    }

    generatedInterface ("ExtendableFluentMatcher") {
        interfaces << clFluentMatcher.withArgs("Value", "Match")
        interfaces << "ExtendableFluent".asClass().withArgs("Value", "This")
        typeParameters << ["Value", "Match", 
                "This extends ExtendableFluentMatcher<Value, Match, This>"]

        def fluentMethods = clFluentMatcher.methods.grep { it.returns.name.endsWith("FluentMatcher") && it.name != "as" }
        methods(fluentMethods) {
            annotations << "Override"
            returns = "This"
            tags.remove(tags.size()-1)
        }

        def propertyMethods = clFluentAssert.methods.grep { it.returns.name.endsWith("Property") }
        methods(propertyMethods) {
            annotations << "Override"
            returns = clFluentProperty.withArgs(typeParameters[0].name, "This")
            tags.remove(tags.size()-1)
        }
    }
}