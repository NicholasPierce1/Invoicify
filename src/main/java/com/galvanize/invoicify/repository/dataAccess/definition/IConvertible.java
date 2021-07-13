package com.galvanize.invoicify.repository.dataAccess.definition;

import java.util.function.Supplier;

public interface IConvertible<T> {

    public abstract void createDataAccess(final Object[] dbo);

    public abstract T convertTo(final Supplier<T> supplier);

}
