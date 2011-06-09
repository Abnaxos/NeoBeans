package ch.raffael.neobeans.impl;

import org.jetbrains.annotations.NotNull;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public interface StorageDelegateFactory {

    @NotNull
    StorageDelegate createStorageDelegate(@NotNull Class<?> beanClass);

}
