package com.galvanize.invoicify.repository.dataaccess.definition;

import java.util.function.Supplier;

public interface IConvertible<T> {

    public abstract void createDataAccess(final Object[] dbo);

    public abstract <M extends T> M convertTo(final Supplier<M> supplier);

    public abstract <M extends T> void convertToModel(final M modelObject);

}
