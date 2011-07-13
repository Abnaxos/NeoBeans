package ch.raffael.neobeans.impl;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import ch.raffael.neobeans.Converter;
import ch.raffael.neobeans.InvalidBeanException;

import static ch.raffael.neobeans.temp.NotImplementedException.*;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class ConverterResolver {

    private final Map<Class<?>, Converter> cache = new HashMap<Class<?>, Converter>();

    @NotNull
    public Converter converterFor(@NotNull Class<?> beanClass, @NotNull Class<?> clazz, Class<? extends Converter> declared) {
        // goals:
        // - handle arrays
        // - handle collections (collection <=> array)
        // - arrays and collections supported also, if an explicit converter is given
        // - ? should a converter declare itself array/collection compatible?
        // - handle enums automatically (use String)
        // - try to find a constructor with a single String argument, use that and toString()
        boolean array = false;
        if ( clazz.isArray() ) {
            if ( clazz.getComponentType().isArray() ) {
                throw new InvalidBeanException(beanClass, "Arrays of arrays not supported");
            }
            array = true;
            clazz = clazz.getComponentType();
        }
        return notImplemented();
    }

}
