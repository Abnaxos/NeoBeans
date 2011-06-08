package ch.raffael.neobeans;

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public interface Converter {

    Object fromNeo4j(Object value);

    Object toNeo4j(Object value);

}
