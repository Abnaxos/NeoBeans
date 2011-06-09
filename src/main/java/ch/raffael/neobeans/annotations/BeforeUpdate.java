package ch.raffael.neobeans.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Declares a method to be called before storing a NeoBean. If {@link #onCreate()} is
 * <code>true</code>, the method will also be called if the node is being created.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BeforeUpdate {

    /**
     * Whether to call the method also on node creation. If <code>true</code>, it will be
     * called <em>after</em> methods annotated with {@link BeforeCreate}.
     *
     * @return <code>true</code> if the method should be called upon node creation.
     */
    boolean onCreate() default false;

}
