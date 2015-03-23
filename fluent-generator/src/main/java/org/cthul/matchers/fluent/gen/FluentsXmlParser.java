package org.cthul.matchers.fluent.gen;

import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.cthul.matchers.fluent.gen.FluentsGenerator.AssertConfig;
import org.cthul.matchers.fluent.gen.FluentsGenerator.FluentConfig;

public class FluentsXmlParser {
    
    private final List<FluentConfig> factories = new ArrayList<>();
    private final List<AssertConfig> asserts = new ArrayList<>();

    public List<FluentConfig> getFactories() {
        return factories;
    }

    public List<AssertConfig> getAsserts() {
        return asserts;
    }
    
    public void parse(XMLStreamReader xml) throws XMLStreamException {    
        try {
            xml.nextTag();
            xml.require(XMLStreamReader.START_ELEMENT, null, "fluents");
            while (xml.nextTag() == XMLStreamReader.START_ELEMENT) {
                switch (xml.getLocalName()) {
                    case "fluent":
                        factories.add(readFluentConfig(xml));
                        break;
                    case "assert":
                        asserts.add(readAssertConfig(xml));
                        break;
                    default:
                        throw new XMLStreamException(
                                "Expected <fluent> or <assert>, got: " + xml.getLocalName(),
                                xml.getLocation());
                }
            }
            xml.require(XMLStreamReader.END_ELEMENT, null, "fluents");
        } catch (XMLStreamException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new XMLStreamException(e.getMessage(), xml.getLocation(), e);
        } finally {
            xml.close();
        }
    }
    
    private FluentConfig readFluentConfig(XMLStreamReader xml) throws XMLStreamException {
        String name = xml.getAttributeValue(null, "name");
        String impl = xml.getAttributeValue(null, "impl");
        String type = xml.getAttributeValue(null, "type");
        String ext = xml.getAttributeValue(null, "extends");
        String extra = xml.getAttributeValue(null, "extraParams");
        if (impl != null) {
            impl = genericFix(impl);
            if (name == null) name = impl;
        }
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
                case "adapter":
                    String aType = xml.getAttributeValue(null, "type");
                    flc.setAdapterType(aType);
                    xml.nextTag();
                    break;
                case "extends":
                    flc.getExtends().add(genericFix(xml.getElementText()));
                    break;
                case "factory":
                    flc.getFactories().add(readFactoryConfig(xml));
                    break;
                default:
                    throw new XMLStreamException(
                            "Expected <adapter>, <extends> or <factory>, got: " + xml.getLocalName(),
                            xml.getLocation());
            }
        }
        flc.setImplemented(impl != null);
        return flc;
    }

    private FluentsGenerator.FactoryConfig readFactoryConfig(XMLStreamReader xml) throws XMLStreamException {
        String clazz = xml.getAttributeValue(null, "class");
        FluentsGenerator.FactoryConfig fac = new FluentsGenerator.FactoryConfig(clazz);
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
             }
        }
        return fac;
    }

    private AssertConfig readAssertConfig(XMLStreamReader xml) throws XMLStreamException {
        String name = xml.getAttributeValue(null, "name");
        AssertConfig asc = new AssertConfig(name);
        
        while (xml.nextTag() == XMLStreamReader.START_ELEMENT) {
            switch (xml.getLocalName()) {
                case "extends":
                    asc.getExtends().add(genericFix(xml.getElementText()));
                    break;
             }
        }
        
        xml.require(XMLStreamReader.END_ELEMENT, null, "assert");
        return asc;
    }

    private static String genericFix(String s) {
        if (s == null) return null;
        return s.replace('{', '<').replace('}', '>');
    }
}
