package ch.raffael.neobeans.impl;

import java.net.MalformedURLException;
import java.net.URL;

import ch.raffael.neobeans.ConvertException;
import ch.raffael.neobeans.Converter;
import ch.raffael.neobeans.NodeKey;


/**
* @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
*/
public class TestBean {

    public static final Converter URL_CONVERTER = new Converter() {
        @Override
        public Object fromNeo4j(Object value) {
            try {
                return new URL((String)value);
            }
            catch ( MalformedURLException e ) {
                throw new ConvertException(value, e);
            }
        }

        @Override
        public Object toNeo4j(Object value) {
            return value.toString();
        }
    };
    private NodeKey key;
    private String name;
    private URL homepage;

    public NodeKey getKey() {
        return key;
    }

    public void setKey(NodeKey key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public URL getHomepage() {
        return homepage;
    }

    public void setHomepage(URL homepage) {
        this.homepage = homepage;
    }
}
