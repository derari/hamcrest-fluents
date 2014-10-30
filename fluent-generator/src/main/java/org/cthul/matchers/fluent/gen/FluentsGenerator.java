package org.cthul.matchers.fluent.gen;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaParameterizedType;
import com.thoughtworks.qdox.model.JavaType;
import com.thoughtworks.qdox.model.JavaTypeVariable;
import com.thoughtworks.qdox.model.JavaWildcardType;
import com.thoughtworks.qdox.model.impl.DefaultJavaParameter;
import com.thoughtworks.qdox.model.impl.DefaultJavaParameterizedType;
import com.thoughtworks.qdox.model.impl.DefaultJavaType;
import com.thoughtworks.qdox.model.impl.DefaultJavaTypeVariable;
import com.thoughtworks.qdox.model.impl.DefaultJavaWildcardType;
import com.thoughtworks.qdox.model.impl.DefaultJavaWildcardType.BoundType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.cthul.api4j.gen.GeneratedMethod;

public class FluentsGenerator {
    
    private final List<FluentConfig> configs;
    private JavaClass matcherClass = null;
    private Exception suppressed = null;

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
    
    public void generate(Api4JConfiguration cfg, String path) {
        new Api1(cfg.getRootContext().subcontext(path)).run((api) -> {
            Template staticCall = api.getTemplate("staticCall");
            Map<String, Object> argMap = new HashMap<>();
            matcherClass = asClass("org.hamcrest.Matcher");
            
            for (FluentConfig fc: configs) {
                
                GeneratedClass cg = fc.getName() == null ? 
                        api.createClass() : api.createClass(fc.getName());
                cg.setInterface(true);
                List<String> typeParameters = configureTypeParameters(fc, cg);
                
                add(cg.getInterfaces(), 
                        "org.cthul.matchers.fluent.ext.ExtendableFluentProperty<Value, Property, ThisFluent, This>");
                for (String sup: fc.getExtends()) {
                    sup = sup.replace("...", "Value, Property, ThisFluent, This");
                    if (!sup.contains("<")) {
                        sup += "<Value, Property, ThisFluent, This>";
                    }
                    add(cg.getInterfaces(), sup);
                }
                
                for (FactoryConfig fac: fc.getFactories()) {
                    Map<Pattern, String> renameMap = fac.getRenamePatternMap();
                    fac.factoryMethods().forEach((factory) -> {
                        suppressed = null;
                        GeneratedMethod flMethod = method(cg, factory);
                        renameMap.entrySet().forEach(e -> {
                            String s = e.getKey().matcher(flMethod.getName())
                                    .replaceAll(e.getValue());
                            flMethod.setName(s);
                        });
                        if (isMatcherFactory(factory)) {
                            setReturns(flMethod, "ThisFluent");
                            setModifiers(flMethod, "default");
                            argMap.put("method", factory);
                            setBody(flMethod, 
                                    "return __(" + 
                                        staticCall.generate(argMap) + 
                                    ");");
                            flMethod.getTags().removeAll(flMethod.getTagsByName("return"));
                            add(flMethod.getTags(), "return fluent");
                            fixTypeParameters(fac, flMethod, typeParameters);
                        } else {
                            throw new RuntimeException(
                                    "Can't handle " + factory, suppressed);
                        }
                    });
                }
            }
        });
    }

    private List<String> configureTypeParameters(FluentConfig fc, GeneratedClass cg) {
        List<String> args = new ArrayList<>();
        List<String> afterValue = new ArrayList<>();
        List<String> afterProperty = new ArrayList<>();
        Pattern pValue = Pattern.compile("\\bValue\\b");
        Pattern pProperty = Pattern.compile("\\bProperty\\b");
        for (String extra: fc.getExtraParams()) {
            if (pProperty.matcher(extra).find()) {
                afterProperty.add(extra);
            } else if (pValue.matcher(extra).find()) {
                afterValue.add(extra);
            } else {
                args.add(parameterToArg(extra));
                add(cg.getTypeParameters(), extra);
            }
        }
        
        add(cg.getTypeParameters(), "Value");
        addAll(cg.getTypeParameters(), afterValue);
        add(cg.getTypeParameters(), fc.getType() == null ? "Property" : "Property extends " + fc.getType());
        addAll(cg.getTypeParameters(), afterProperty);
        
        args.add("Value");
        afterValue.stream().forEach(p -> args.add(parameterToArg(p)));
        args.add("Property");
        afterProperty.stream().forEach(p -> args.add(parameterToArg(p)));
        args.add("ThisFluent");
        args.add("This");
        
        StringBuilder sb = new StringBuilder();
        sb.append("<");
        args.forEach(s -> sb.append(s).append(","));
        sb.setCharAt(sb.length()-1, '>');
        
        addAll(cg.getTypeParameters(),
                "ThisFluent extends org.cthul.matchers.fluent.Fluent<Value>",
                "This extends " + cg.getName() + sb);
        
        return args;
    }
    
    private String parameterToArg(String s) {
        int iSpace = s.indexOf(' ');
        if (iSpace < 0) return s;
        return s.substring(0, iSpace);
    }
    
    private void fixTypeParameters(FactoryConfig fac, GeneratedMethod flMethod, List<String> typeParameters) {
        Map<String, String> map = fac.getTypeParameterMap();
        List<JavaTypeVariable<JavaMethod>> tParams = flMethod.getTypeParameters();
        if (map.isEmpty() && !tParams.isEmpty()) {
            map.put(tParams.get(0).getName(), "Property");
        }
        tParams = replaceTypeParameterList(tParams, map);
        if (tParams == null) {
            flMethod.getTypeParameters().removeIf(p -> typeParameters.contains(p.getName()));
        } else {
            tParams.removeIf(p -> typeParameters.contains(p.getName()));
            flMethod.getTypeParameters().clear();
            flMethod.getTypeParameters().addAll(tParams);

            for (int i = 0; i < flMethod.getParameters().size(); i++) {
                JavaParameter p = flMethod.getParameters().get(i);
                JavaClass newType = replaceTypeParameter(p.getType(), map);
                if (newType != null) {
                    p = new DefaultJavaParameter(newType, p.getName(), p.isVarArgs());
                    flMethod.getParameters().set(i, p);
                }
            }
        }
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
    
    public static class FluentConfig {
        private final String name;
        private final String type;
        private final List<String> extraParams;
        private final List<String> extendz;
        private final List<FactoryConfig> factories;

        public FluentConfig(String name, String type) {
            this.name = name;
            this.type = type;
            this.extraParams = new ArrayList<>();
            this.extendz = new ArrayList<>();
            this.factories = new ArrayList<>();
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

        public String getType() {
            return type;
        }

        public List<FactoryConfig> getFactories() {
            return factories;
        }
        
//        Stream<JavaMethod> factoryMethods() {
//            return factories.stream()
//                    .flatMap(FactoryConfig::factoryMethods);
//        }
    }
    
    public static class FactoryConfig {
        private final String clazz;
        private final List<String> includes;
        private final List<String> excludes;
        private final Map<String, String> typeParameterMap;
        private final Map<String, String> renameMap;

        public FactoryConfig(String clazz) {
            this.clazz = clazz;
            includes = new ArrayList<>(3);
            excludes = new ArrayList<>(3);
            typeParameterMap = new HashMap<>();
            renameMap = new HashMap<>();
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
