def coreMethodDefs
def prefixes

def noPrefixCoreMethods = [
    [name:"not", ret:"This", sig:"",
        comment:"Negates the next matcher's match result and prepends \"not\" to its description.",
        tags:[]]
    ]

def prefixedCoreMethods = [
    [name:"__", ret:"Fluent", sig:"org.hamcrest.Matcher<Property> matcher",
        comment:"Adds a matcher to the fluent.\nActual behavior is implementation specific.\n<p>\nAfter the call, the fluent chain goes back to the value itself.",
        tags:["param matcher the matcher"]],
]

return [core: coreMethodDefs]