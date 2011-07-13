package ch.raffael.neobeans.impl;

import java.net.MalformedURLException;
import java.net.URL;

import ch.raffael.neobeans.ConvertException;
import ch.raffael.neobeans.Converter;


/**
* @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
*/
public class UrlConverter implements Converter {

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
}
