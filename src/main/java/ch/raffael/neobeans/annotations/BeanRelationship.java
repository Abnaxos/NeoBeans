package ch.raffael.neobeans.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.neo4j.graphdb.Direction;


/**
 * Annotates a property as representing a relationship.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BeanRelationship {

    Class<?> type();

    Direction direction() default Direction.OUTGOING;

    /**
     * If <code>true</code>, synchronize on store. This will update all relationships
     * contained in the property's value (creating the ones that are not present yet) and
     * delete the remaining relationships. If <code>false</code>, the relationships will
     * be read, only, updates to the relationships must be done manually.
     *
     * @return <code>true</code> to synchronize the relationships on store.
     */
    boolean sync() default false;

}
