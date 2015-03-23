package org.cthul.matchers.fluent.gen;

import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaParameterizedType;
import com.thoughtworks.qdox.model.JavaType;
import com.thoughtworks.qdox.model.JavaTypeVariable;
import com.thoughtworks.qdox.model.JavaWildcardType;
import com.thoughtworks.qdox.model.impl.DefaultDocletTag;
import com.thoughtworks.qdox.model.impl.DefaultJavaField;
import com.thoughtworks.qdox.model.impl.DefaultJavaParameter;
import com.thoughtworks.qdox.model.impl.DefaultJavaParameterizedType;
import com.thoughtworks.qdox.model.impl.DefaultJavaType;
import com.thoughtworks.qdox.model.impl.DefaultJavaTypeVariable;
import com.thoughtworks.qdox.model.impl.DefaultJavaWildcardType;
import com.thoughtworks.qdox.model.impl.DefaultJavaWildcardType.BoundType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.cthul.api4j.Api4JConfiguration;
import org.cthul.api4j.api.Template;
import org.cthul.api4j.api1.Api1;
import static org.cthul.api4j.api1.QdoxTools.*;
import org.cthul.api4j.gen.GeneratedClass;
import org.cthul.api4j.gen.GeneratedConstructor;
import org.cthul.api4j.gen.GeneratedMethod;
import org.cthul.strings.JavaNames;

/**
 * Generates continuous fluents and assert methods.
 */
public class FluentsGenerator {
    
    private final List<FluentConfig> fluents;
    private final List<AssertConfig> asserts;
    private JavaClass matcherClass = null;
    private JavaClass adapterClass = null;
    private JavaClass valueClass = null;
    private Exception suppressed = null;
    private Map<JavaType, FluentConfig> fluentTypeMap = null;

    public FluentsGenerator(List<FluentConfig> fluents, List<AssertConfig> asserts) {
        this.fluents = fluents;
        this.asserts = asserts;
    }
    
    /**
     * A method is a matcher factory if it returns 
     * a {@code org.hamcrest.Matcher} or subclass.
     */
    private boolean isMatcherFactory(JavaMethod jm) {
        try {
            JavaClass ret = jm.getReturns();
            return ret.isA(matcherClass);
        } catch (Exception e) {
            suppressed = e;
            return false;
        }
    }
    
    /**
     * A method is an adapter factory if it returns 
     * a {@code org.cthul.matchers.fluent.value.MatchValueAdapter} or subclass.
     */
    private boolean isAdapterFactory(JavaMethod jm) {
        try {
            JavaClass ret = jm.getReturns();
            return ret.isA(adapterClass);
        } catch (Exception e) {
            suppressed = e;
            return false;
        }
    }
    
    /**
     * A method is an value factory if it returns 
     * a {@code org.cthul.matchers.fluent.value.MatchValue} or subclass.
     */
    private boolean isValueFactory(JavaMethod jm) {
        try {
            JavaClass ret = jm.getReturns();
            return ret.isA(valueClass);
        } catch (Exception e) {
            suppressed = e;
            return false;
        }
    }
    
    /**
     * Generates the configured fluents and asserts.
     * @param cfg api4j settings
     * @param path target path
     */
    public void generate(Api4JConfiguration cfg, String path) {
        new Api1(cfg.getRootContext().subcontext(path)).run((api) -> {
            matcherClass = asClass("org.hamcrest.Matcher");
            adapterClass = asClass("org.cthul.matchers.fluent.value.MatchValueAdapter");
            valueClass = asClass("org.cthul.matchers.fluent.value.MatchValue");
            initFluentTypeMap();
            
            for (FluentConfig fc: fluents) {
                generateFluent(fc, api);
            }
            
            for (AssertConfig ac: asserts) {
                generateAssert(ac, api);
            }
        });
    }
    
    private void initFluentTypeMap() {
        fluentTypeMap = new HashMap<>();
        for (FluentConfig fc: fluents) {
            JavaType jt = asClass(fc.getType());
            fluentTypeMap.put(jt, fc);
        }
    }

    private void generateFluent(FluentConfig fc, Api1 api) {
        if (fc.isImplemented()) return;
        
        Template staticCall = api.getTemplate("staticCall");
        Map<String, Object> argMap = new HashMap<>();
        GeneratedClass iBase = fc.getName() == null ?
                generatedInterface(api) : generatedInterface(api, fc.getName());
        GeneratedClass iOrChain = nestedInterface(iBase, "OrChain");
        GeneratedClass iAndChain = nestedInterface(iBase, "AndChain");
//        GeneratedClass iDownCast = nestedInterface(iBase, "DownCast");
//        GeneratedClass iStep = nestedInterface(iBase, "Step");
        GeneratedClass cStep = nestedClass(iBase, "Step");
        GeneratedClass cAssert = nestedClass(iBase, "Assert");
                
        List<String> typeArguments = new ArrayList<>();
        List<String> typeParameters = new ArrayList<>();
        
        configureTypeParameters(fc, iBase, typeArguments, typeParameters);
        setChainTypeParamters(iOrChain, typeArguments, typeParameters);
        setChainTypeParamters(iAndChain, typeArguments, typeParameters);
        setImplTypeParamters(cStep, typeArguments, typeParameters);
        cAssert.getTypeParameters().addAll(fc.getExtraParams());
        cAssert.getTypeParameters().add("Value extends " + fc.getType());
        
        List<String> assertArgs = parametersToArgs(fc.getExtraParams());
        assertArgs.add("Value");
        String cAssertWithArgs = "Assert<" + String.join(",", assertArgs) + ">";

        iBase.getInterfaces().add(
                "org.cthul.matchers.fluent.ext.ExtensibleFluentStep<Value, BaseFluent, TheFluent, This>");
        iOrChain.getInterfaces().add(
                "org.cthul.matchers.fluent.ext.ExtensibleFluentStep.OrChain<Value, TheFluent, This>");
        iOrChain.getInterfaces().add(asTheFluent(iBase, typeArguments));
        iAndChain.getInterfaces().add(
                "org.cthul.matchers.fluent.ext.ExtensibleFluentStep.AndChain<Value, TheFluent, This>");
        iAndChain.getInterfaces().add(asTheFluent(iBase, typeArguments));
        cStep.getInterfaces().add(asStepFluent(iBase, typeArguments));
        cStep.setSuperClass(
                withArgs(
                    asClass("org.cthul.matchers.fluent.builder.FluentStepBuilder"),
                            "Value", "TheFluent", "This"));
        cAssert.getInterfaces().add(asTheFluent(iBase, typeArguments, cAssertWithArgs));
        cAssert.setSuperClass(
                withArgs(
                    asClass("org.cthul.matchers.fluent.builder.FluentAssertBuilder"),
                            "Value", cAssertWithArgs));
        
        GeneratedConstructor newStep = constructor(cStep, "public (org.cthul.matchers.fluent.builder.Matchable<? extends Value, TheFluent> matchable)");
        newStep.setSourceCode("super(matchable);");
        GeneratedConstructor newAssertPublic = constructor(cAssert);
        setSignature(newAssertPublic, 
                "org.cthul.matchers.fluent.builder.FailureHandler handler, " +
                withArgs(valueClass, "Value").getGenericFullyQualifiedName() + " value");
        newAssertPublic.setSourceCode("super(handler, value);");
        GeneratedConstructor newAssertPrivate = constructor(cAssert, "protected");
        setSignature(newAssertPrivate, 
                "org.cthul.matchers.fluent.builder.Matchable<? extends Value, ?> value");
        newAssertPrivate.setSourceCode("super(value);");

        for (String sup: fc.getExtends()) {
            sup = sup.replace("...", "Value, BaseFluent, TheFluent, This");
            if (!sup.contains("<")) {
                sup += "<Value, BaseFluent, TheFluent, This>";
            }
            add(iBase.getInterfaces(), sup);
            
            int iGen = sup.indexOf('<');
            String supName = sup.substring(0, iGen);
            String genSig = sup.substring(iGen).replace("BaseFluent, ", "");
            String supOr = supName + ".OrChain" + genSig;
            add(iOrChain.getInterfaces(), supOr);
            String supAnd = supName + ".AndChain" + genSig;
            add(iAndChain.getInterfaces(), supAnd);
        }
        
        for (FactoryConfig fac: fc.getFactories()) {
            Map<Pattern, String> renameMap = fac.getRenamePatternMap();
            fac.factoryMethods().forEach((factory) -> {
                suppressed = null;
                GeneratedMethod flMethod = method(iBase, factory);
                renameMap.entrySet().forEach(e -> {
                    String s = e.getKey().matcher(flMethod.getName())
                            .replaceAll(e.getValue());
                    flMethod.setName(s);
                });
                if (isMatcherFactory(factory)) {
                    setReturns(flMethod, "TheFluent");
                    setModifiers(flMethod, "default");
                    argMap.put("method", factory);
                    setBody(flMethod,
                            "return __(" +
                                    staticCall.generate(argMap) +
                                    ");");
                    flMethod.getTags().removeAll(flMethod.getTagsByName("return"));
                    flMethod.getTags().add("return fluent");
                    fixTypeParameters(fac, flMethod, typeArguments);
                    
                } else if (isAdapterFactory(factory)) {
                    JavaType adaptedType = resolveReturnTypeArgument(factory, adapterClass, 1);
                    Map<String,String> typeParamMap = fac.getTypeParameterMap(flMethod);
                    if (adaptedType.getFullyQualifiedName().contains(" extends")) {
                        String[] parts = adaptedType.getFullyQualifiedName().split(" extends ", 2);
                        if (fac.getTypeParameterMap().isEmpty()) {
                            typeParamMap = new HashMap<>(typeParamMap);
                            typeParamMap.put(parts[0], parts[0]);
                        }
                    }
                    String stepType = getFluentStepType(adaptedType);
                    int iGeneric = stepType.indexOf('<');
                    String stepClass = iGeneric < 0 ? stepType : stepType.substring(0, iGeneric);
                    
                    setReturns(flMethod, stepType);
                    setModifiers(flMethod, "default");
                    argMap.put("method", factory);
//                    setBody(flMethod,
//                            "return org.cthul.matchers.fluent.ext.ExtensibleStepAdapter.New.__adapt(" +
//                                    "this, " +
//                                    staticCall.generate(argMap) + ", " + 
//                                    stepClass + ".Impl.adapter());");
                    setBody(flMethod,
                            "return as(org.cthul.matchers.fluent.ext.Extensions.uncheckedFactory(" +
                                    staticCall.generate(argMap) + ", " + 
                                    stepClass + ".Step.adapter()));");
                    flMethod.getTags().removeAll(flMethod.getTagsByName("return"));
                    flMethod.getTags().add("return " + JavaNames.under_score(stepClass).replace('_', ' ') + " step");
                    fixTypeParameters(flMethod, typeParameters, typeParamMap);
                } else if (isValueFactory(factory)) {
                    // skip, nothing to do here
                    iBase.getMethods().remove(flMethod);
                } else {
                    String methodName;
                    try {
                        methodName = factory.toString();
                    } catch (Exception e) {
                        try {
                            methodName = factory.getCallSignature();
                        } catch (Exception e2) {
                            methodName = factory.getName();
                        }
                    }
                    String msg = "Can't create fluent for\n    " + methodName;
//                    System.err.println(msg);
//                    iBase.getMethods().remove(flMethod);
                    throw new RuntimeException(msg, suppressed);
                }
            });
        }
        
        addChainMethods(iBase, iOrChain, typeArguments, "either", "or");
        addChainImplementation(iBase, cStep, typeArguments, "TheFluent", "Value", "OrChain", "either");
        addChainImplementation(iBase, cAssert, typeArguments, cAssertWithArgs, "Value", "OrChain", "either");
        addChainMethods(iBase, iAndChain, typeArguments, "both", "and");
        addChainImplementation(iBase, cStep, typeArguments, "TheFluent", "Value", "AndChain", "both");
        addChainImplementation(iBase, cAssert, typeArguments, cAssertWithArgs, "Value", "AndChain", "both");
        addChainMethods(iBase, iAndChain, typeArguments, "all");
        addChainImplementation(iBase, cStep, typeArguments, "TheFluent", "Value", "AndChain", "all", false);
        addChainImplementation(iBase, cAssert, typeArguments, cAssertWithArgs, "Value", "AndChain", "all", false);
        
        
        
        Set<String> castingMethods = new HashSet<>();
        for (FluentConfig subFc: fluents) {
            JavaClass subValueClass = subFc.getValueClass();
            if (subFc == fc || !subValueClass.isA(fc.getValueClass())) continue;
            String name = "as";
            if (subFc.isImplemented()) {
                String type = subFc.getType();
                if (type.contains("<")) type = type.substring(0, type.indexOf('<'));
                if (type.contains(".")) type = type.substring(type.lastIndexOf('.')+1);
                name += type;
            } else {
                name += subFc.getCastMethodName();  
            }
            if (!castingMethods.add(name)) continue;
                
                //JavaType adaptedType = resolveReturnTypeArgument(factory, adapterClass, 1);
                //Map<String,String> typeParamMap = fc.getTypeParameterMap(castMethod);
//                if (adaptedType.getFullyQualifiedName().contains(" extends")) {
//                    String[] parts = adaptedType.getFullyQualifiedName().split(" extends ", 2);
//                    if (fac.getTypeParameterMap().isEmpty()) {
//                        typeParamMap = new HashMap<>(typeParamMap);
//                        typeParamMap.put(parts[0], parts[0]);
//                    }
//                }
            String stepType = getFluentStepType(subFc, subFc.getType(), "TheFluent,?");
            int iGeneric = stepType.indexOf('<');
            String subFcClass = iGeneric < 0 ? stepType : stepType.substring(0, iGeneric);

            GeneratedMethod mCastStep = method(cStep, name);
            mCastStep.getTypeParameters().addAll(subFc.getExtraParams());
            setReturns(mCastStep, stepType);
            setModifiers(mCastStep, "public");
            setBody(mCastStep,
                    "return as(" + subFcClass + ".adapter());");
            mCastStep.setComment("Converts this fluent into a " + subFcClass);
            mCastStep.getTags().add("return " + JavaNames.under_score(subFcClass).replace('_', ' ') + " step");
//                fixTypeParameters(flMethod, typeParameters, typeParamMap);
            
            String assertType = getFluentAssertType(subFc, subFc.getType());
            GeneratedMethod mAssertStep = method(cAssert, name);
            mAssertStep.getTypeParameters().addAll(subFc.getExtraParams());
            setReturns(mAssertStep, assertType);
            setModifiers(mAssertStep, "public");
            setBody(mAssertStep,
                    "return as(" + subFcClass + ".adapter());");
            mAssertStep.setComment("Converts this fluent into a " + subFcClass);
            mAssertStep.getTags().add("return " + JavaNames.under_score(subFcClass).replace('_', ' ') + " fluent");
        }
        
        DefaultJavaField fXAdapter = field(cStep, "private static final org.cthul.matchers.fluent.ext.ExtensionFactory X_ADAPTER");
        fXAdapter.setInitializationExpression(
                "org.cthul.matchers.fluent.ext.Extensions.typecastFactory(" +
                fc.getAdapterTypeName() + ".class, " +
                "Step::new, Assert::new)");
        
        GeneratedMethod mAdapter = method(cStep, "adapter");
        mAdapter.getModifiers().add("public static");
        mAdapter.getTypeParameters().addAll(fc.getExtraParams());
        mAdapter.getTypeParameters().add("Value extends " + fc.getType());
        mAdapter.getTypeParameters().add("TheFluent");
        mAdapter.setReturns(getAdapterType(cStep, cAssert));
        mAdapter.setSourceCode("return X_ADAPTER;");
    }
    
    private void configureTypeParameters(FluentConfig fc, GeneratedClass cg) {
        configureTypeParameters(fc, cg, new ArrayList<>(), new ArrayList<>());
    }

    private void configureTypeParameters(FluentConfig fc, GeneratedClass cg, List<String> typeArguments, List<String> typeParameters) {
        List<String> afterValue = new ArrayList<>();
        Pattern pValue = Pattern.compile("\\bValue\\b");
        for (String extra: fc.getExtraParams()) {
            if (pValue.matcher(extra).find()) {
                afterValue.add(extra);
            } else {
                typeParameters.add(extra);
            }
        }
        
        typeParameters.add(fc.getType() == null ? "Value" : "Value extends " + fc.getType());
        typeParameters.addAll(afterValue);
        typeParameters.add("BaseFluent");
        typeParameters.add("TheFluent extends BaseFluent");
        typeParameters.stream()
                .map(this::parameterToArg)
                .forEach(typeArguments::add);
        
        typeArguments.add("This");
        typeParameters.add("This extends " + cg.getName() + toGenericSig(typeArguments));
        
        cg.getTypeParameters().addAll(typeParameters);
    }
    
    private String toGenericSig(List<String> params) {
        StringBuilder sb = new StringBuilder();
        sb.append('<');
        params.forEach(s -> sb.append(s).append(','));
        sb.setCharAt(sb.length()-1, '>');
        return sb.toString();
    }
    
    private void setImplTypeParamters(GeneratedClass cStep, List<String> typeArgs, List<String> typeParams) {
        int i = typeArgs.indexOf("BaseFluent");
        List<String> a = new ArrayList<>(typeArgs);
        a.remove(i);
        List<String> p = new ArrayList<>(typeParams);
        p.remove(i);
        p.set(i, "TheFluent");
        p.set(p.size()-1, "This extends " + cStep.getName() + toGenericSig(a));
        cStep.getTypeParameters().addAll(p);
    }
    
    private void setChainTypeParamters(GeneratedClass gc, List<String> typeArgs, List<String> typeParams) {
        int i = typeArgs.indexOf("BaseFluent");
        List<String> a = new ArrayList<>(typeArgs);
        a.remove(i);
        List<String> p = new ArrayList<>(typeParams);
        p.remove(i);
        p.set(i, "TheFluent");
        p.set(p.size()-1, "This extends " + gc.getName() + toGenericSig(a));
        gc.getTypeParameters().addAll(p);
    }
    
    private JavaClass asTheFluent(JavaClass clazz, List<String> typeArgs) {
        return asTheFluent(clazz, typeArgs, "This");
    }
    
    private JavaClass asTheFluent(JavaClass clazz, List<String> typeArgs, String thisArg) {
        int i = typeArgs.indexOf("BaseFluent");
        List<String> a = new ArrayList<>(typeArgs);
        a.set(i, "org.cthul.matchers.fluent.Fluent<Value>");
        a.set(i+1, thisArg);
        a.set(i+2, thisArg);
        return (JavaClass) withArgs(clazz, a);
    }
    
    private JavaClass asStepFluent(JavaClass clazz, List<String> typeArgs) {
        int i = typeArgs.indexOf("BaseFluent");
        List<String> a = new ArrayList<>(typeArgs);
        a.set(i, "TheFluent");
        a.set(i+1, "TheFluent");
        return (JavaClass) withArgs(clazz, a);
    }
    
    private JavaClass chainFluent(JavaClass clazz, List<String> typeArgs, String theFluentArg) {
        int i = typeArgs.indexOf("BaseFluent");
        List<String> a = new ArrayList<>(typeArgs);
        a.remove(i);
        a.set(i, theFluentArg);
        a.set(a.size()-1, "?");
        return (JavaClass) withArgs(clazz, a);
    }
    
    private JavaClass chainTerminal(JavaClass clazz, List<String> typeArgs) {
        int i = typeArgs.indexOf("BaseFluent");
        List<String> a = new ArrayList<>(typeArgs);
        a.set(i, "TheFluent");
        a.set(i+1, "TheFluent");
        a.set(a.size()-1, "?");
        return (JavaClass) withArgs(clazz, a);
    }
    
    private List<String> parametersToArgs(List<String> list) {
        return list.stream().map(this::parameterToArg)
                .collect(Collectors.toList());
    }
    
    private String parameterToArg(String s) {
        int iSpace = s.indexOf(' ');
        if (iSpace < 0) return s;
        return s.substring(0, iSpace);
    }
    
    private void fixTypeParameters(FactoryConfig fac, GeneratedMethod flMethod, List<String> typeParameters) {
        fixTypeParameters(flMethod, typeParameters, fac.getTypeParameterMap(flMethod));
    }
    
    private void fixTypeParameters(GeneratedMethod flMethod, List<String> typeParameters, Map<String, String> map) {
        List<JavaTypeVariable<JavaMethod>> tParams = flMethod.getTypeParameters();
        tParams = replaceTypeParameterList(tParams, map);
        if (tParams == null) {
            removeClassParameters(flMethod.getTypeParameters(), typeParameters);
        } else {
            removeClassParameters(tParams, typeParameters);
            flMethod.getTypeParameters().clear();
            Set<String> paramNames = new HashSet<>();
            for (JavaTypeVariable<JavaMethod> v: tParams) {
                if (paramNames.add(v.getName())) {
                    flMethod.getTypeParameters().add(v);
                }
            }

            for (int i = 0; i < flMethod.getParameters().size(); i++) {
                JavaParameter p = flMethod.getParameters().get(i);
                JavaClass newType = replaceTypeParameter(p.getType(), map);
                if (newType != null) {
                    p = new DefaultJavaParameter(newType, p.getName(), p.isVarArgs());
                    flMethod.getParameters().set(i, p);
                }
            }
        }
        for (DocletTag tag: flMethod.getTagsByName("param")) {
            String v = tag.getValue();
            if (v == null || v.isEmpty()) continue;
            int iOpen = v.indexOf('<');
            int iClose = v.indexOf('>');
            if (iOpen < 0 || iClose < 0 || iClose < iOpen) continue;
            String p = v.substring(iOpen+1, iClose);
            String r = map.get(p);
            if (r == null) continue;
            if (typeParameters.contains(r)) {
                flMethod.getTags().remove(tag);
            } else {
                v = v.substring(0, iOpen+1) + r + v.substring(iClose);
                int tagIndex = flMethod.getTags().indexOf(tag);
                tag = new DefaultDocletTag("param", v);
                flMethod.getTags().set(tagIndex, tag);
            }
        }
    }
    
    private void removeClassParameters(List<JavaTypeVariable<JavaMethod>> params, List<String> classParams) {
        params.removeIf(p -> {
            return classParams.stream().anyMatch(
                    c -> (c.equals(p.getName()) || c.startsWith(p.getName() + " ")));
        });
    }
    
    private JavaClass replaceTypeParameter(JavaType clazz, Map<String, String> map) {
        String replacement = map.get(clazz.getCanonicalName());
        if (clazz instanceof JavaWildcardType) {
            String n = clazz.getGenericFullyQualifiedName();
            String n2 = n;
            for (Map.Entry<String, String> e: map.entrySet()) {
                n2 = n.replaceAll("\\b"+e.getKey()+"\\b", e.getValue());
            }
            if (!n.equals(n2)) {
                BoundType bt;
                int iExt = n2.indexOf(" extends ");
                if (iExt < 0) {
                    bt = BoundType.SUPER;
                    iExt = n2.indexOf(" super ") + " super ".length();
                } else {
                    bt = BoundType.EXTENDS;
                    iExt += " extends ".length();
                }
                clazz = asClass(n2.substring(iExt));
                DefaultJavaWildcardType fixed = new DefaultJavaWildcardType(clazz, bt);
                return fixed;
            }
        }
        if (clazz instanceof JavaTypeVariable) {
            JavaTypeVariable<?> jtv = (JavaTypeVariable) clazz;
            List<JavaType> types = replaceTypeParameterList(jtv.getBounds(), map);
            if (types != null || replacement != null) {
                DefaultJavaTypeVariable<?> fixed = new DefaultJavaTypeVariable<>(
                        replacement != null ? replacement : jtv.getName(), 
                        jtv.getGenericDeclaration());
                fixed.setBounds(types != null ? types : jtv.getBounds());
                return fixed;
            }
        }
        if (clazz instanceof JavaParameterizedType) {
            JavaParameterizedType jpt = (JavaParameterizedType) clazz;
            int dim = ((DefaultJavaType) clazz).getDimensions();
            List<JavaType> types = jpt.getActualTypeArguments();
            types = replaceTypeParameterList(types, map);
            if (types != null || replacement != null) {
                DefaultJavaParameterizedType fixed = new DefaultJavaParameterizedType(
                        replacement != null ? replacement : jpt.getFullyQualifiedName(), 
                        dim) {
                    @Override
                    public String getCanonicalName() {
                        try {
                            return super.getCanonicalName();
                        } catch (Exception e) {
                            // hotfix
                            return replacement != null ? replacement : jpt.getFullyQualifiedName();
                        }
                    }
                };
                fixed.setActualArgumentTypes(types != null ? types : jpt.getActualTypeArguments());
                return fixed;
            }
        }
        if (replacement != null) {
            return asClass(replacement);
        }
        return null;
    }
    
    private <JT extends JavaType> List<JT> replaceTypeParameterList(List<JT> classes, Map<String, String> map) {
        if (classes == null) return null;
        boolean original = true;
        for (int i = 0; i < classes.size(); i++) {
            JavaType t = replaceTypeParameter(classes.get(i), map);
            if (t != null) {
                if (original) classes = new ArrayList<>(classes);
                original = false;
                classes.set(i, (JT) t);
            }
        }
        if (original) return null;
        return classes;
    }
    
    private void addChainMethods(GeneratedClass iBase, GeneratedClass iChain, List<String> typeArgs, String name) {
        addChainMethods(iBase, iChain, typeArgs, name, null, false);
    }
    
    private void addChainMethods(GeneratedClass iBase, GeneratedClass iChain, List<String> typeArgs, String name, String terminal) {
        addChainMethods(iBase, iChain, typeArgs, name, terminal, true);
    }
    
    private void addChainMethods(GeneratedClass iBase, GeneratedClass iChain, List<String> typeArgs, String name, String terminal, boolean matcherMethod) {
        String chainType = iChain.getName(); // JavaNames.CamelCase(terminal + " chain");
        JavaClass chainClass = chainFluent(iChain, typeArgs, "TheFluent"); //chainFluent(chainType, typeArgs);
        JavaClass chainTerminal = chainTerminal(iBase, typeArgs);
        GeneratedMethod gm;
        
        gm = method(iBase, name);
        gm.getAnnotations().add("Override");
        gm.setReturns(chainClass);
        
        if (matcherMethod) {
            gm = method(iBase, name);
            gm.getAnnotations().add("Override");
            gm.setReturns(chainClass);
            setSignature(gm, "org.hamcrest.Matcher<? super Value> matcher");

        }
        if (terminal != null) {
            gm = method(iChain, terminal);
            gm.getAnnotations().add("Override");
            gm.setReturns(chainTerminal);

            gm = method(iChain, terminal);
            gm.getAnnotations().add("Override");
            setReturns(gm, "TheFluent");
            setSignature(gm, "org.hamcrest.Matcher<? super Value> matcher");
        }
////        chainClass = chainFluent(chainType, typeArgs);
//        gm = method(cStep, name);
//        gm.getAnnotations().add("Override");
//        gm.getModifiers().add("public");
//        gm.setReturns(chainClass);
//        gm.setSourceCode("return (" + chainType + ") super." + name + "();");
//        
//        gm = method(cStep, name);
//        gm.getAnnotations().add("Override");
//        gm.getModifiers().add("public");
//        gm.setReturns(chainClass);
//        setSignature(gm, "org.hamcrest.Matcher<? super Value> matcher");
//        gm.setSourceCode("return (" + chainType + ") super." + name + "(matcher);");
    }
    
    private void addChainImplementation(JavaClass iBase, GeneratedClass cStep, List<String> typeArgs, String theFluentArg, String valueType, String chainType, String name) {
        addChainImplementation(iBase, cStep, typeArgs, theFluentArg, valueType, chainType, name, true);
    }
    
    private void addChainImplementation(JavaClass iBase, GeneratedClass cStep, List<String> typeArgs, String theFluentArg, String valueType, String chainType, String name, boolean withMatcherArg) {
        String baseName = iBase.getFullyQualifiedName();
        chainType = (baseName.contains("<") ? baseName.substring(0, baseName.indexOf('<')) : baseName) + "." + chainType;
//        String chainType = iChain.getName(); // JavaNames.CamelCase(terminal + " chain");
        JavaClass iChain = asClass(chainType);
        JavaClass chainClass = chainFluent(iChain, typeArgs, theFluentArg); //chainFluent(chainType, typeArgs);
        
        GeneratedMethod gm;
//        chainClass = chainFluent(chainType, typeArgs);
        gm = method(cStep, name);
        gm.getAnnotations().addAll("Override");
        gm.getModifiers().add("public");
        gm.setReturns(chainClass);
        gm.setSourceCode("return _" + name + "(" +
                    chainType + ".class, Step.adapter());");
//        gm.getAnnotations().addAll("Override", "SuppressWarnings(\"unchecked\")");
//        gm.getModifiers().add("public");
//        gm.setReturns(chainClass);
//        gm.setSourceCode("return (" + chainType + ") super." + name + "();");
        
        if (withMatcherArg) {
            gm = method(cStep, name);
            gm.getAnnotations().addAll("Override");
            gm.getModifiers().add("public");
            gm.setReturns(chainClass);
            gm.setSourceCode("return " + name + "().__(matcher);");
    //        gm.getAnnotations().addAll("Override", "SuppressWarnings(\"unchecked\")");
    //        gm.getModifiers().add("public");
    //        gm.setReturns(chainClass);
    //        gm.setSourceCode("return (" + chainType + ") super." + name + "(matcher);");
            setSignature(gm, "org.hamcrest.Matcher<? super " + valueType + "> matcher");
        }
    }
    
    private JavaClass getAdapterType(FluentConfig fc) {
        List<String> args = new ArrayList<>();
        args.addAll(parametersToArgs(fc.getExtraParams()));
        args.add("Value");
        List<String> stepArgs = new ArrayList<>(args);
        stepArgs.addAll(Arrays.asList("TheFluent", "?"));
        JavaClass cStep = (JavaClass) withArgs(asClass(fc.getName()+".Step"), stepArgs);
        JavaClass cAssert = (JavaClass) withArgs(asClass(fc.getName()+".Assert"), args);
        return getAdapterType(cStep, cAssert);
    }
    
    private JavaClass getAdapterType(JavaClass cStep, JavaClass cFluent) {
        List<String> args = new ArrayList<>();
        args.add("java.lang.Object");
        List<String> fluentArgs = new ArrayList<>();
        for (JavaTypeVariable<?> v: cFluent.getTypeParameters()) {
            String n = parameterToArg(v.getName());
            fluentArgs.add(n);
        }
        args.add(withArgs(cFluent, fluentArgs).getGenericFullyQualifiedName());
        args.add("TheFluent");
        List<String> stepArgs = new ArrayList<>();
        for (JavaTypeVariable<?> v: cStep.getTypeParameters()) {
            String n = parameterToArg(v.getName());
            if (n.equals("This")) n = "?";
            stepArgs.add(n);
        }
        args.add(withArgs(cStep, stepArgs).getGenericFullyQualifiedName());
        return (JavaClass) withArgs(asClass("org.cthul.matchers.fluent.ext.ExtensionFactory"), args);
    }
    
    private String getFluentStepType(JavaType valueType) {
        String typeName = valueType.getGenericFullyQualifiedName();
        String typeArg = typeName;
        if (typeName.contains(" extends ")) {
            String[] parts = typeName.split(" extends ", 2);
            typeArg = parts[0];
//            typeName = parts[1];
        }
        
        FluentConfig stepConfig = findConfigFor((JavaClass) valueType);
        if (stepConfig == null) {
            stepConfig = findConfigFor(asClass("java.lang.Object"));
        }
        return getFluentStepType(stepConfig, typeArg, "TheFluent,?");
    }
    
    private String getFluentStepType(FluentConfig stepConfig, String typeArg, String moreArgs) {
        return getFluentStepType(stepConfig, ".Step", typeArg, moreArgs);
    }
    
    private String getFluentAssertType(FluentConfig stepConfig, String typeArg) {
        return getFluentStepType(stepConfig, ".Assert", typeArg, "");
    }
    
    private String getFluentStepType(FluentConfig stepConfig, String implClass, String typeArg, String moreArgs) {
        if (!moreArgs.isEmpty()) moreArgs = "," + moreArgs;
        String fluent;
        if (stepConfig != null) {
            fluent = stepConfig.getName();
            if (fluent.contains("...")) {
                fluent = fluent.replace("...", typeArg + moreArgs);
            }
            if (fluent.contains("<")) {
                fluent = fluent.replaceAll("[^\\d\\w_$]Value\\B", typeArg);
            }
        } else {
            fluent = "org.cthul.matchers.fluent8.ObjectFluent";
        }
        if (fluent.contains("<")) {
            int i = fluent.indexOf('<');
            fluent = fluent.substring(0, i) + implClass + fluent.substring(i);
        } else {
            fluent += implClass + "<";
            if (stepConfig != null && !stepConfig.getExtraParams().isEmpty()) {
                fluent += stepConfig.getExtraParams().stream()
                        .map(s -> s.contains(" extends ") ? s.substring(0, s.indexOf(" extends ")) : s)
                        .collect(Collectors.joining(","))
                        + ",";
            }
            fluent += typeArg + moreArgs + ">";
        }
        return fluent;
    }
    
    private FluentConfig findConfigFor(JavaClass valueClass) {
        String typeString = valueClass.getGenericFullyQualifiedName();
        JavaClass bestMatch = null;
        FluentConfig stepConfig = null;
        for (FluentConfig cfg: fluents) {
            if (cfg.getType().equals(typeString)) {
                stepConfig = cfg;
                break;
            }
            if (valueClass.isA(asClass(cfg.getType()))) {
                if (bestMatch != null && 
                        (cfg.getType().equals("java.lang.Object")
                         || bestMatch.isA(cfg.getValueClass()))) {
                    continue;
                }
                bestMatch = cfg.getValueClass();
                stepConfig = cfg;
            }
        }
        return stepConfig;
    }
    
    private void generateAssert(AssertConfig cfg, Api1 api) {
        GeneratedClass iAssert = generatedInterface(api, cfg.getName());
        iAssert.getInterfaces().addAll(cfg.getExtends());
        
        Set<String> names = new HashSet<>();
        for (FluentConfig fc: fluents) {
            if (fc.isImplemented()) continue;
            String name = fc.getName();
            if (name.endsWith("Fluent")) name = name.substring(0, name.length() - 6);
            if (names.contains(name)) {
                name = fc.getType();
                if (name.contains("<")) name = name.substring(0, name.indexOf('<'));
                name = name.substring(name.lastIndexOf('.')+1);
            }
            name = JavaNames.CamelCase(name);
            if (!names.add(name)) continue;
            String adapterName = (name.matches("([AEIOU]).*") ? "an" : "a") + name;
            
            GeneratedMethod mAdapter = method(iAssert, adapterName);
            mAdapter.getModifiers().add("public static");
            mAdapter.getTypeParameters().addAll(fc.getExtraParams());
            mAdapter.getTypeParameters().add("Value extends " + fc.getType());
            mAdapter.getTypeParameters().add("TheFluent");
            mAdapter.setReturns(getAdapterType(fc));
            mAdapter.setSourceCode("return " + fc.getName() + ".Step.adapter();");            
            
            List<String> extraParams = fc.getExtraParams();
            extraParams.add("Value extends " + fc.getType());
            List<String> extraArgs = parametersToArgs(extraParams);
            String fluentClassName = fc.getName() + ".Assert";
                        
            GeneratedMethod mAssert = method(iAssert, "public static assertThat(Value value)");
            mAssert.setReturns((JavaClass) withArgs(asClass(fluentClassName), extraArgs));
            mAssert.getTypeParameters().addAll(extraParams);
            
            mAssert.setBody(
                    "return new " +
                        fluentClassName + "<>" +
                        "(org.cthul.matchers.fluent.builder.AssertionErrorHandler.instance(), " +
                        "org.cthul.matchers.fluent.adapters.IdentityValue.value" +
                        "(value));");
        }
    }
    
    private JavaClass getFluentType(JavaClass impl, FluentConfig fc) {
        JavaType cFluent = withArgs(asClass("org.cthul.matchers.fluent.Fluent"), fc.getType());
        List<Object> args = new ArrayList<>();
        args.addAll(parametersToArgs(fc.getExtraParams()));
        args.addAll(Arrays.asList(fc.getType(), cFluent, impl, impl));
        return (JavaClass) withArgs(
                asClass(fc.getName()), 
                args);
    }
    
    public static class FluentConfig {
        private final String name;
        private final String type;
        private final List<String> extraParams;
        private final List<String> extendz;
        private final List<FactoryConfig> factories;
        private boolean isImplemented = false;
        private String adapterType;
        private JavaClass valueClass;

        public FluentConfig(String name, String type) {
            this.name = name;
            this.type = type != null ? type : "java.lang.Object";
            this.extraParams = new ArrayList<>(3);
            this.extendz = new ArrayList<>(3);
            this.factories = new ArrayList<>(3);
        }

        public String getName() {
            return name;
        }

        public List<String> getExtraParams() {
            return extraParams;
        }

        public List<String> getExtends() {
            return extendz;
        }

        public void setImplemented(boolean isImplemented) {
            if (isImplemented) {
                if (!factories.isEmpty()) {
                    throw new IllegalArgumentException(
                            "Implementation reference can not define factories: " +
                            name + " -> " + factories);
                }
            }
            this.isImplemented = isImplemented;
        }

        public boolean isImplemented() {
            return isImplemented;
        }

        public String getType() {
            return type;
        }

        public JavaClass getValueClass() {
            if (valueClass == null) {
                valueClass = asType(getType());
            }
            return valueClass;
        }

        public List<FactoryConfig> getFactories() {
            if (isImplemented && !factories.isEmpty()) {
                throw new IllegalArgumentException(
                        "Implementation reference can not define factories: " +
                        name + " -> " + factories);
            }
            return factories;
        }
        
//        Stream<JavaMethod> factoryMethods() {
//            return factories.stream()
//                    .flatMap(FactoryConfig::factoryMethods);
//        }

        public String getAdapterType() {
            return adapterType;
        }

        public void setAdapterType(String adapterType) {
            this.adapterType = adapterType;
        }
        
        public String getAdapterTypeName() {
            if (adapterType != null) {
                return adapterType;
            }
            String t = type;
            int i = t.indexOf('<');
            if (i > 0) t = t.substring(0, i).trim();
            return t;
        }

        private String getCastMethodName() {
            String cast = getName();
            if (cast.contains("<")) cast = cast.substring(0, cast.indexOf('<'));
            if (cast.contains(".")) cast = cast.substring(cast.lastIndexOf('.')+1);
            if (cast.endsWith("Fluent")) cast = cast.substring(0, cast.length() - "Fluent".length());
            return cast;
        }
    }
    
    public static class FactoryConfig {
        private final String clazz;
        private final List<String> includes;
        private final List<String> excludes;
        private final Map<String, String> typeParameterMap;
        private final Map<String, String> renameMap;
        private String adapterType = null;

        public FactoryConfig(String clazz) {
            this.clazz = clazz;
            includes = new ArrayList<>(3);
            excludes = new ArrayList<>(3);
            typeParameterMap = new HashMap<>(3);
            renameMap = new HashMap<>(3);
        }

        public String getClazz() {
            return clazz;
        }

        public List<String> getIncludes() {
            return includes;
        }

        public List<String> getExcludes() {
            return excludes;
        }

        public Map<String, String> getTypeParameterMap() {
            return typeParameterMap;
        }

        public Map<String, String> getTypeParameterMap(JavaMethod jm) {
            Map<String, String> map = getTypeParameterMap();
            List<JavaTypeVariable<JavaMethod>> tParams = jm.getTypeParameters();
            if (map.isEmpty() && !tParams.isEmpty()) {
                map = new HashMap<>();
                map.put(tParams.get(0).getName(), "Value");
            }
            return map;
        }

        public Map<String, String> getRenameMap() {
            return renameMap;
        }
        
        public Map<Pattern, String> getRenamePatternMap() {
            Map<Pattern, String> result = new HashMap<>();
            renameMap.entrySet().forEach(e -> result.put(Pattern.compile(e.getKey()), e.getValue()));
            return result;
        }
        
        Stream<JavaMethod> factoryMethods() {
            Stream<JavaMethod> methods = asClass(clazz).getMethods().stream()
                    .filter(JavaMethod::isStatic)
                    .filter(JavaMethod::isPublic)
                    .filter(m -> hasAnnotation(m, ".Factory"))
                    .filter(matcher(includes, false))
                    .filter(matcher(excludes, true));
            return nonEmptyStream(methods, () -> {
                throw noFactories(asClass(clazz));
            });
        }
        
        private Predicate<JavaMethod> matcher(List<String> rules, boolean exclude) {
            if (rules.isEmpty()) return jm -> true;
            StringBuilder sb = new StringBuilder();
            rules.stream().forEach(r -> sb.append("(").append(r).append(")|"));
            sb.setLength(sb.length()-1);
            Pattern p = Pattern.compile(sb.toString());
            return jm -> p.matcher(jm.getDeclarationSignature(false)).find() ^ exclude;
        }
        
        private RuntimeException noFactories(JavaClass sourceClass) {
            StringBuilder sb = new StringBuilder();
            sb.append("No factory methods in ")
                    .append(sourceClass.getCanonicalName())
                    .append(".");
            if (!includes.isEmpty()) {
                sb.append("\ninclude: ").append(includes);
            }
            if (!excludes.isEmpty()) {
                sb.append("\nexclude: ").append(includes);
            }
            for (JavaMethod jm: sourceClass.getMethods()) {
                if (jm.isStatic()) {
                    sb.append("\n- ");
                    try {
                        sb.append(jm);
                    } catch (Exception e) {
                        try {
                            sb.append(jm.getDeclarationSignature(true));
                        } catch (Exception e2) {
                            try {
                                sb.append(jm.getName());
                            } catch (Exception e3) {
                                sb.append(e);
                            }
                        }
                    }
                }
            }
            throw new RuntimeException(sb.toString());
        }

        public void setAdapterType(String adapterType) {
            this.adapterType = adapterType;
        }

        public String getAdapterType() {
            if (adapterType == null) {
                return "java.lang.Object";
            }
            return adapterType;
        }
    }
    
    public static class AssertConfig {
        
        private final String name;
        private final List<String> extendz = new ArrayList<>();

        public AssertConfig(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public List<String> getExtends() {
            return extendz;
        }
    }

    private static <T> Stream<T> nonEmptyStream(Stream<T> stream, Supplier<T> defaultValue) {
        List<T> list = stream.collect(Collectors.toList());
        if (list.isEmpty()) list.add(defaultValue.get());
        return list.stream();
    }

    
//    private static <T> Stream<T> nonEmptyStream(Stream<T> stream, Supplier<T> defaultValue) {
//        return StreamSupport.stream(new NonEmptySpliterator<>(stream, defaultValue), stream.isParallel());
//    }
//    
//    private static class NonEmptySpliterator<T> extends DelegatingSpliterator<T> {
//        
//        private final Spliterator<T> delegatee;
//        private final Supplier<T> defaultValue;
//        private boolean afterFirst = false;
//
//        public NonEmptySpliterator(Stream<T> stream, Supplier<T> defaultValue) {
//            this.delegatee = stream.spliterator();
//            this.defaultValue = defaultValue;
//        }
//        
//        @Override
//        protected Spliterator<T> delegatee() {
//            return delegatee;
//        }
//
//        @Override
//        public boolean tryAdvance(Consumer<? super T> action) {
//            if (afterFirst) return super.tryAdvance(action);
//            boolean success = super.tryAdvance(e -> {
//                synchronized (NonEmptySpliterator.this) {
//                    afterFirst = true;
//                }
//                action.accept(e);
//            });
//            synchronized (this) {
//                if (!afterFirst) {
//                    afterFirst = true;
//                    action.accept(defaultValue.get());
//                    return true;
//                }
//            }
//            return success;
//        }
//
//        @Override
//        public void forEachRemaining(Consumer<? super T> action) {
//            if (afterFirst) {
//                super.forEachRemaining(action);
//                return;
//            }
//            if (tryAdvance(action)) {
//                super.forEachRemaining(action);
//            }
//        }
//
//        static final class HoldingConsumer<T> implements Consumer<T> {
//            Object value;
//            @Override
//            public void accept(T value) {
//                this.value = value;
//            }
//        }
//        
//        @Override
//        public Spliterator<T> trySplit() {
//            if (afterFirst) return super.trySplit();
//            // no synchronization needed, in worst case
//            // multiple 1-element spliterators are produced
//            HoldingConsumer<T> first = new HoldingConsumer<>();
//            if (!tryAdvance(first)) return null;
//            int ch = DISTINCT | IMMUTABLE | ORDERED | SORTED;
//            return Spliterators.spliterator(new Object[]{first.value}, ch);
//        }
//    }
//    
//    private static abstract class DelegatingSpliterator<T> implements Spliterator<T> {
//        protected abstract Spliterator<T> delegatee();
//        @Override
//        public boolean tryAdvance(Consumer<? super T> action) {
//            return delegatee().tryAdvance(action);
//        }
//        @Override
//        public void forEachRemaining(Consumer<? super T> action) {
//            delegatee().forEachRemaining(action);
//        }
//        @Override
//        public Spliterator<T> trySplit() {
//            return delegatee().trySplit();
//        }
//        @Override
//        public long estimateSize() {
//            return delegatee().estimateSize();
//        }
//        @Override
//        public long getExactSizeIfKnown() {
//            return delegatee().getExactSizeIfKnown();
//        }
//        @Override
//        public int characteristics() {
//            return delegatee().characteristics();
//        }
//        @Override
//        public boolean hasCharacteristics(int characteristics) {
//            return delegatee().hasCharacteristics(characteristics);
//        }
//        @Override
//        public Comparator<? super T> getComparator() {
//            return delegatee().getComparator();
//        }
//    }
}
