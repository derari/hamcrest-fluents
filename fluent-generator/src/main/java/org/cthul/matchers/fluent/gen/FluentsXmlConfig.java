package org.cthul.matchers.fluent.gen;

import java.io.InputStream;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import org.cthul.api4j.Api4JConfiguration;
import org.cthul.api4j.xml.XmlConfiguration;
import org.cthul.api4j.xml.XmlHandler;

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
            XMLStreamReader xml = f.createXMLStreamReader(in);
            FluentsXmlParser parser = new FluentsXmlParser();
            parser.parse(xml);
            new FluentsGenerator(parser.getFactories(), parser.getAsserts())
                    .generate(cfg, path);
        }
    }
}
