package ch.raffael.neobeans;

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public interface Converter {

    Converter DO_NOTHING = new Converter() {
        @Override
        public Object fromNeo4j(Object value) {
            return value;
        }
        @Override
        public Object toNeo4j(Object value) {
            return value;
        }
    };

    Object fromNeo4j(Object value);

    Object toNeo4j(Object value);

}
