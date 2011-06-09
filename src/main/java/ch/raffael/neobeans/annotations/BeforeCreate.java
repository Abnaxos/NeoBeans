package ch.raffael.neobeans.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Declares a method to be called before creating a NeoBean in the bean store. Methods
 * annotated with this will be called <em>before</em> any methods annotated with
 * {@link BeforeUpdate @BeforeUpdate(onCreate=true)}.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BeforeCreate {

}
