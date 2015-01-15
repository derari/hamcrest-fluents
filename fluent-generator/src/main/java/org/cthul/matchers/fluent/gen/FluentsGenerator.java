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

public class FluentsGenerator {
    
    private final List<FluentConfig> configs;
    private JavaClass matcherClass = null;
    private JavaClass adapterClass = null;
    private JavaClass valueClass = null;
    private Exception suppressed = null;
    private Map<JavaType, FluentConfig> fluentTypeMap = null;

    public FluentsGenerator(List<FluentConfig> configs) {
        this.configs = configs;
    }
    
    private boolean isMatcherFactory(JavaMethod jm) {
        try {
            JavaClass ret = jm.getReturns();
            return ret.isA(matcherClass);
        } catch (Exception e) {
            suppressed = e;
            return false;
        }
    }
    
    private boolean isAdapterFactory(JavaMethod jm) {
        try {
            JavaClass ret = jm.getReturns();
            return ret.isA(adapterClass);
        } catch (Exception e) {
            suppressed = e;
            return false;
        }
    }
    
    private boolean isValueFactory(JavaMethod jm) {
        try {
            JavaClass ret = jm.getReturns();
            return ret.isA(valueClass);
        } catch (Exception e) {
            suppressed = e;
            return false;
        }
    }
    
    public void generate(Api4JConfiguration cfg, String path) {
        new Api1(cfg.getRootContext().subcontext(path)).run((api) -> {
            matcherClass = asClass("org.hamcrest.Matcher");
            adapterClass = asClass("org.cthul.matchers.fluent.value.MatchValueAdapter");
            valueClass = asClass("org.cthul.matchers.fluent.value.MatchValue");
            initFluentTypeMap();
            
            for (FluentConfig fc: configs) {
                generateFluent(fc, api);
            }
        });
    }
    
    private void initFluentTypeMap() {
        fluentTypeMap = new HashMap<>();
        for (FluentConfig fc: configs) {
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
        GeneratedClass iStep = nestedClass(iBase, "Step");
        
        
//        interface OrChain<Value, TheFluent, This extends OrChain<Value, TheFluent, This>> 
//            extends org.cthul.matchers.fluent.ext.ExtensibleFluentStep.OrChain<Value, TheFluent, This>,
//                    ObjectTestFluent<Value, org.cthul.matchers.fluent.Fluent<Value>, This, This> {
//        
        
        List<String> typeArguments = new ArrayList<>();
        List<String> typeParameters = new ArrayList<>();
        
        configureTypeParameters(fc, iBase, typeArguments, typeParameters);
        setStepTypeParamters(iStep, typeArguments, typeParameters);
        setChainTypeParamters(iOrChain, typeArguments, typeParameters);
        
        add(iBase.getInterfaces(),
                "org.cthul.matchers.fluent.ext.ExtensibleFluentStep<Value, BaseFluent, TheFluent, This>");
        add(iOrChain.getInterfaces(),
                "org.cthul.matchers.fluent.ext.ExtensibleFluentStep.OrChain<Value, TheFluent, This>");
        iOrChain.getInterfaces().add(asTheFluent(iBase, typeArguments));
        
        iStep.setSuperClass(
                withArgs(
                    asClass("org.cthul.matchers.fluent.builder.FluentStepBuilder"),
                            "Value", "TheFluent", "This"));
        iStep.getInterfaces().add(asStepFluent(iBase, typeArguments));
        
        GeneratedConstructor newStep = constructor(iStep, "public (org.cthul.matchers.fluent.builder.Matchable<Value, TheFluent> matchable)");
        newStep.setSourceCode("super(matchable);");
        
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
                    setBody(flMethod,
                            "return org.cthul.matchers.fluent.ext.ExtensibleStepAdapter.New.__adapt(" +
                                    "this, " +
                                    staticCall.generate(argMap) + ", " + 
                                    stepClass + ".Step.adapter());");
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

        addChainMethods(iBase, iStep, iOrChain, typeArguments, "either", "or");
        
        DefaultJavaField fXAdapter = field(iStep, "private static final org.cthul.matchers.fluent.ext.ExtensibleStepAdapter X_ADAPTER");
        fXAdapter.setInitializationExpression(
                "org.cthul.matchers.fluent.ext.ExtensibleStepAdapter.New.forType(" +
                fc.getAdapterTypeName() + ".class, " +
                "Step::new);");
        
        GeneratedMethod mAdapter = method(iStep, "adapter");
        mAdapter.getModifiers().add("public static");
        mAdapter.getTypeParameters().addAll(fc.getExtraParams());
        mAdapter.getTypeParameters().add("TheFluent");
        mAdapter.setReturns(getAdapterType(iBase, fc));
        mAdapter.setSourceCode("return X_ADAPTER;");
        
        
//                private static final ExtensibleStepAdapter X_ADAPTER = ExtensibleStepAdapter.New.forType(Comparable.class, Step::new);
//        
//        public static <C extends Comparable<C>, Fl> ExtensibleStepAdapter<Object, C, Fl, ComparableFluent.Step<C, C, Fl, ?>> adapter(Class<C> c) {
//            return X_ADAPTER;
//        }
//
//        public static <C extends Comparable<C>, Fl> ExtensibleStepAdapter<Object, C, Fl, ComparableFluent.Step<C, C, Fl, ?>> adapter() {
//            return X_ADAPTER;
//        }
//

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
    
    private void setStepTypeParamters(GeneratedClass iAssert, List<String> typeArgs, List<String> typeParams) {
        int i = typeArgs.indexOf("BaseFluent");
        List<String> a = new ArrayList<>(typeArgs);
        a.remove(i);
        List<String> p = new ArrayList<>(typeParams);
        p.remove(i);
        p.set(i, "TheFluent");
        p.set(p.size()-1, "This extends Step" + toGenericSig(a));
        iAssert.getTypeParameters().addAll(p);
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
        int i = typeArgs.indexOf("BaseFluent");
        List<String> a = new ArrayList<>(typeArgs);
        a.set(i, "org.cthul.matchers.fluent.Fluent<Value>");
        a.set(i+1, "This");
        return (JavaClass) withArgs(clazz, a);
    }
    
    private JavaClass asStepFluent(JavaClass clazz, List<String> typeArgs) {
        int i = typeArgs.indexOf("BaseFluent");
        List<String> a = new ArrayList<>(typeArgs);
        a.set(i, "TheFluent");
        a.set(i+1, "TheFluent");
        return (JavaClass) withArgs(clazz, a);
    }
    
    private JavaClass chainFluent(JavaClass clazz, List<String> typeArgs) {
        int i = typeArgs.indexOf("BaseFluent");
        List<String> a = new ArrayList<>(typeArgs);
        a.remove(i);
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
    
    private void addChainMethods(GeneratedClass iBase, GeneratedClass iStep, GeneratedClass iChain, List<String> typeArgs, String name, String terminal) {
        String chainType = iChain.getName(); // JavaNames.CamelCase(terminal + " chain");
        JavaClass chainClass = chainFluent(iChain, typeArgs); //chainFluent(chainType, typeArgs);
        JavaClass chainTerminal = chainTerminal(iBase, typeArgs);
        GeneratedMethod gm;
        
        gm = method(iBase, name);
        gm.getAnnotations().add("Override");
        gm.setReturns(chainClass);
        
        gm = method(iBase, name);
        gm.getAnnotations().add("Override");
        gm.setReturns(chainClass);
        setSignature(gm, "org.hamcrest.Matcher<? super Value> matcher");
        
        gm = method(iChain, terminal);
        gm.getAnnotations().add("Override");
        gm.setReturns(chainTerminal);
        
        gm = method(iChain, terminal);
        gm.getAnnotations().add("Override");
        setReturns(gm, "TheFluent");
        setSignature(gm, "org.hamcrest.Matcher<? super Value> matcher");
        
        chainType = iBase.getName() + "." + chainType;
//        chainClass = chainFluent(chainType, typeArgs);
        gm = method(iStep, name);
        gm.getAnnotations().add("Override");
        gm.getModifiers().add("public");
        gm.setReturns(chainClass);
        gm.setSourceCode("return (" + chainType + ") super." + name + "();");
        
        gm = method(iStep, name);
        gm.getAnnotations().add("Override");
        gm.getModifiers().add("public");
        gm.setReturns(chainClass);
        setSignature(gm, "org.hamcrest.Matcher<? super Value> matcher");
        gm.setSourceCode("return (" + chainType + ") super." + name + "(matcher);");
    }
    
    private JavaClass getAdapterType(JavaClass iFluent, FluentConfig fc) {
        List<String> args = new ArrayList<>();
        args.add("java.lang.Object");
        args.add(fc.getType());
        args.add("TheFluent");
        List<String> stepArgs = new ArrayList<>();
        for (JavaTypeVariable<?> v: iFluent.getTypeParameters()) {
            String n = parameterToArg(v.getName());
            switch (n) {
                case "Value":
                    stepArgs.add(fc.getType());
                    break;
                case "BaseFluent":
                    stepArgs.add("TheFluent");
                    break;
                case "This":
                    stepArgs.add("?");
                    break;
                default:
                    stepArgs.add(n);
                    break;
            }
        }
        args.add(withArgs(iFluent, stepArgs).getGenericFullyQualifiedName());
        return (JavaClass) withArgs(asClass("org.cthul.matchers.fluent.ext.ExtensibleStepAdapter"), args);
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
        String fluent;
        if (stepConfig != null) {
            fluent = stepConfig.getName();
            if (fluent.contains("...")) {
                fluent = fluent.replace("...", typeArg + ",This,This,?");
            }
            if (fluent.contains("<")) {
                fluent = fluent.replaceAll("[^\\d\\w_$]Value\\B", typeArg);
            }
        } else {
            fluent = "org.cthul.matchers.fluent8.ObjectFluent";
        }
        if (!fluent.contains("<")) {
            fluent += "<" + typeArg + ",This,This,?>";
        }
        return fluent;
    }
    
    private FluentConfig findConfigFor(JavaClass valueClass) {
        String typeString = valueClass.getGenericFullyQualifiedName();
        JavaClass bestMatch = null;
        FluentConfig stepConfig = null;
        for (FluentConfig cfg: configs) {
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
                valueClass = (JavaClass) asType(getType());
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
