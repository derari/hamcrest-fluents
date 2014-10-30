package org.cthul.matchers.fluent.gen;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.cthul.api4j.Api4JConfiguration;
import org.cthul.api4j.xml.XmlConfiguration;
import org.cthul.api4j.xml.XmlHandler;
import org.cthul.matchers.fluent.gen.FluentsGenerator.FactoryConfig;
import org.cthul.matchers.fluent.gen.FluentsGenerator.FluentConfig;

public class FluentsXmlConfig implements XmlConfiguration {

    @Override
    public XmlHandler accept(String namespace, String element) {
        if (namespace == null || "".equals(namespace)) {
            if ("fluents".equals(element)) {
                return Handler.INSTANCE;
            }
        }
        return null;
    }

    public static class Handler implements XmlHandler {
        
        public static final Handler INSTANCE = new Handler();

        private final XMLInputFactory f = XMLInputFactory.newFactory();
        
        @Override
        public void handle(Api4JConfiguration cfg, String path, InputStream in) throws Exception {
            List<FluentConfig> configs = new ArrayList<>();
            
            XMLStreamReader xml = f.createXMLStreamReader(in);
            xml.nextTag();
            xml.require(XMLStreamReader.START_ELEMENT, null, "fluents");
            while (xml.nextTag() == XMLStreamReader.START_ELEMENT) {
                switch (xml.getLocalName()) {
                    case "fluent":
                        configs.add(readFluentConfig(xml));
                        break;
                    case "assert":
                        
                        break;
                    default:
                        throw new XMLStreamException(
                                "Expected <fluent> or <assert>, got: " + xml.getLocalName(),
                                xml.getLocation());
                }
            }
            xml.require(XMLStreamReader.END_ELEMENT, null, "fluents");
            
            new FluentsGenerator(configs).generate(cfg, path);
        }
        
        private FluentConfig readFluentConfig(XMLStreamReader xml) throws XMLStreamException {
            String name = xml.getAttributeValue(null, "name");
            String type = xml.getAttributeValue(null, "type");
            String ext = xml.getAttributeValue(null, "extends");
            String extra = xml.getAttributeValue(null, "extraParams");
            FluentConfig flc = new FluentConfig(name, genericFix(type));
            if (ext != null) flc.getExtends().add(genericFix(ext));
            if (extra != null) {
                extra = genericFix(extra);
                for (String s: extra.split(",")) {
                    if (!s.isEmpty()) flc.getExtraParams().add(s);
                }
            }
            while (xml.nextTag() == XMLStreamReader.START_ELEMENT) {
                switch (xml.getLocalName()) {
                    case "extends":
                        flc.getExtends().add(genericFix(xml.getElementText()));
                        break;
                    case "factory":
                        flc.getFactories().add(readFactoryConfig(xml));
                        break;
                    default:
                        throw new XMLStreamException(
                                "Expected <extends> or <factory>, got: " + xml.getLocalName(),
                                xml.getLocation());
                }
            }
            return flc;
        }
        
        private FactoryConfig readFactoryConfig(XMLStreamReader xml) throws XMLStreamException {
            String clazz = xml.getAttributeValue(null, "class");
            FactoryConfig fac = new FactoryConfig(clazz);
            String s = xml.getAttributeValue(null, "include");
            if (s != null) fac.getIncludes().add(s);
            s = xml.getAttributeValue(null, "exclude");
            if (s != null) fac.getExcludes().add(s);
            
            while (xml.nextTag() == XMLStreamReader.START_ELEMENT) {
                switch (xml.getLocalName()) {
                    case "include":
                        fac.getIncludes().add(xml.getElementText());
                        break;
                    case "exclude":
                        fac.getExcludes().add(xml.getElementText());
                        break;
                    case "rename": {
                        String from = xml.getAttributeValue(null, "from");
                        String to = xml.getAttributeValue(null, "to");
                        fac.getRenameMap().put(from, to);
                        xml.nextTag();
                        break; }
                    case "mapTypeParameter": {
                        String from = xml.getAttributeValue(null, "from");
                        String to = xml.getAttributeValue(null, "to");
                        fac.getTypeParameterMap().put(genericFix(from), genericFix(to));
                        xml.nextTag();
                        break; }
                    case "disableTypeParameterMapping":
                        fac.getTypeParameterMap().put("---", "---");
                        xml.nextTag();
                        break;
                    default:
                        throw new XMLStreamException(
                                "Expected <include>, <exclude>, <rename>, "
                                        + "<mapTypeParameter>, "
                                        + "or <disableTypeParameterMapping>; "
                                        + "got: " + xml.getLocalName(),
                                xml.getLocation());
                }
            }
            return fac;
        }
        
        private static String genericFix(String s) {
            if (s == null) return null;
            return s.replace('{', '<').replace('}', '>');
        }
    }
}
