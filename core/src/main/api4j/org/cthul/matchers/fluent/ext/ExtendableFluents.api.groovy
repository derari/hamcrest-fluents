api1 {

    def clFluentProperty = "org.cthul.matchers.fluent.FluentProperty".asClass()
    def clFluent = "org.cthul.matchers.fluent.Fluent".asClass()
    def clFluentPropertyAssert = "org.cthul.matchers.fluent.FluentPropertyAssert".asClass()
    def clFluentAssert = "org.cthul.matchers.fluent.FluentAssert".asClass()
    def clFluentPropertyMatcher = "org.cthul.matchers.fluent.FluentPropertyMatcher".asClass()
    def clFluentMatcher = "org.cthul.matchers.fluent.FluentMatcher".asClass()

    generatedInterface ("ExtendableFluentProperty") {
        interfaces << clFluentProperty.withArgs("Value", "Property")
        typeParameters << ["Value", "Property", 
                "ThisFluent extends org.cthul.matchers.fluent.Fluent<Value>", 
                "This extends ExtendableFluentProperty<Value, Property, ThisFluent, This>"]

        def propMethods = clFluentProperty.methods.grep { it.returns.name.endsWith("Property") && it.typeParameters.empty }
        methods(propMethods) {
            annotations << "Override"
            returns = "This"
            tags.remove(tags.size()-1)
        }

        def fluentMethods = clFluentProperty.methods.grep { it.returns.name.endsWith("Fluent") }
        methods(fluentMethods) {
            annotations << "Override"
            returns = "ThisFluent"
            tags.remove(tags.size()-1)
        }
    }

    generatedInterface ("ExtendableFluent") {
        interfaces << clFluent.withArgs("Value")
        interfaces << "ExtendableFluentProperty".asClass().withArgs("Value", "Value", "This", "This")
        typeParameters << ["Value", 
                "This extends ExtendableFluent<Value, This>"]

        def fluentMethods = clFluent.methods.grep { it.returns.name.endsWith("Fluent") }
        methods(fluentMethods) {
            annotations << "Override"
            returns = "This"
            tags.remove(tags.size()-1)
        }
    }

    generatedInterface ("ExtendableFluentPropertyAssert") {
        interfaces << clFluentPropertyAssert.withArgs("Value", "Property")
        interfaces << "ExtendableFluentProperty".asClass().withArgs("Value", "Property", "ThisFluent", "This")
        typeParameters << ["Value", "Property", 
                "ThisFluent extends org.cthul.matchers.fluent.FluentAssert<Value>", 
                "This extends ExtendableFluentPropertyAssert<Value, Property, ThisFluent, This>"]

        def propMethods = clFluentPropertyAssert.methods.grep { it.returns.name.endsWith("PropertyAssert") && it.typeParameters.empty }
        methods(propMethods) {
            annotations << "Override"
            returns = "This"
            tags.remove(tags.size()-1)
        }

        def fluentMethods = clFluentPropertyAssert.methods.grep { it.returns.name.endsWith("FluentAssert") }
        methods(fluentMethods) {
            annotations << "Override"
            returns = "ThisFluent"
            tags.remove(tags.size()-1)
        }
    }

    generatedInterface ("ExtendableFluentAssert") {
        interfaces << clFluentAssert.withArgs("Value")
        interfaces << "ExtendableFluent".asClass().withArgs("Value", "This")
        interfaces << "ExtendableFluentPropertyAssert".asClass().withArgs("Value", "Value", "This", "This")
        typeParameters << ["Value", 
                "This extends ExtendableFluentAssert<Value, This>"]

        def fluentMethods = clFluentAssert.methods.grep { it.returns.name.endsWith("FluentAssert") && it.name != "as" }
        methods(fluentMethods) {
            annotations << "Override"
            returns = "This"
            tags.remove(tags.size()-1)
        }
    }

    generatedInterface ("ExtendableFluentPropertyMatcher") {
        interfaces << clFluentPropertyMatcher.withArgs("Value", "Property", "Match")
        interfaces << "ExtendableFluentProperty".asClass().withArgs("Value", "Property", "ThisFluent", "This")
        typeParameters << ["Value", "Property", "Match",
                "ThisFluent extends org.cthul.matchers.fluent.FluentMatcher<Value, Match>", 
                "This extends ExtendableFluentPropertyMatcher<Value, Property, Match, ThisFluent, This>"]

        def propMethods = clFluentPropertyMatcher.methods.grep { it.returns.name.endsWith("PropertyMatcher") && it.typeParameters.empty }
        methods(propMethods) {
            annotations << "Override"
            returns = "This"
            tags.remove(tags.size()-1)
        }

        def fluentMethods = clFluentPropertyMatcher.methods.grep { it.returns.name.endsWith("FluentMatcher") }
        methods(fluentMethods) {
            annotations << "Override"
            returns = "ThisFluent"
            tags.remove(tags.size()-1)
        }
    }

    generatedInterface ("ExtendableFluentMatcher") {
        interfaces << clFluentMatcher.withArgs("Value", "Match")
        interfaces << "ExtendableFluent".asClass().withArgs("Value", "This")
        interfaces << "ExtendableFluentPropertyMatcher".asClass().withArgs("Value", "Value", "Match", "This", "This")
        typeParameters << ["Value", "Match", 
                "This extends ExtendableFluentMatcher<Value, Match, This>"]

        def fluentMethods = clFluentMatcher.methods.grep { it.returns.name.endsWith("FluentMatcher") }
        methods(fluentMethods) {
            annotations << "Override"
            returns = "This"
            tags.remove(tags.size()-1)
        }
    }
}