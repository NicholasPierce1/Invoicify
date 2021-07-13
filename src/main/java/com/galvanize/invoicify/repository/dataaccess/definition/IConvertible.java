package com.galvanize.invoicify.repository.dataaccess.definition;

import java.util.function.Supplier;

public interface IConvertible<T> {

    public abstract void createDataAccess(final Object[] dbo);

    public abstract T convertTo(final Supplier<T> supplier);

}
