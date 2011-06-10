package ch.raffael.neobeans.impl;

import org.jetbrains.annotations.NotNull;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public interface BeanMappingFactory {

    @NotNull
    Object mappingKey(@NotNull Object bean);

    @NotNull
    BeanMapping createBeanMapping(@NotNull Object bean);

}
