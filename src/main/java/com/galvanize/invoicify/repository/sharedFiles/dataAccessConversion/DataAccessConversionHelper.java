package com.galvanize.invoicify.repository.sharedFiles.dataAccessConversion;

import com.galvanize.invoicify.repository.dataAccess.definition.IDataAccess;
import com.sun.istack.NotNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * <h1>DataAccessConversionHelper</h1>
 * <h2>Type: Class</h2>
 *
 * Implementing Helper methods to create data access objects(DAOs)
 * from the data from the database
 */
@Component
public final class DataAccessConversionHelper {

    /**
     *<p>
     * This is a Helper method that takes the data from the database and
     * the constructor of the specified entity to create multiple data access objects
     *</p>
     *
     * @param data: Object[] data representing the information from database.
     * @param dataList: generic list of data access objects
     * @param typeConstructor: Lambda typeConstructor representing the constructor for the specified entity type.
     */
    public <U extends List<T>, T extends IDataAccess<?>> void createDataAccessObjects(
            @NotNull final List<? extends Object[]> data,
            @NotNull final U dataList,
            @NotNull final Supplier<T> typeConstructor)
            throws DataAccessConversionException, IllegalArgumentException {
        // making sure the list is == 0 before adding DAOs
        if(dataList.size() != 0)
            throw new IllegalArgumentException("list size of data access must be 0");
        // loops through data and adds each DAO created to the list
        try{
            for (final Object[] objectData : data) {
                dataList.add(this.createDataAccessObject(objectData, typeConstructor));
            }
        }
        // checks to make sure DDL is matches up properly
        catch(Exception e){
            throw new DataAccessConversionException("" +
                    "DataAccess type conversion failed. Make sure Object[] matches the column" +
                    "order of the table's DDL implementation and that the DataAccessConversion implementation" +
                    "matches the table's DDL implementation.\n" + e.getLocalizedMessage());
        }

    }


    /**
     *<p>
     * This is a Helper method that takes the data from the database and
     * the constructor of the specified entity to create a data access object
     *</p>
     *
     * @param data: Object[] data representing the information from database.
     * @param typeConstructor: Lambda typeConstructor representing the constructor for the specified entity type.
     * @return  Returning the created data access object
     */
    public <T extends IDataAccess<?>> T createDataAccessObject(
            @NotNull final Object[] data,
            @NotNull final Supplier<T> typeConstructor)
            throws DataAccessConversionException, IllegalArgumentException {
        // takes the specified type and then updates the DAO with the data
        T returnValue = typeConstructor.get();
        returnValue.createDataAccess(data);
        return returnValue;

    }

    /**
     * todo: nick add doc
     * @param dataAccessList
     * @param modelSupplier
     * @param <T>
     * @param <K>
     * @param <M>
     * @return
     */
    public <T extends List<K>, K extends IDataAccess<M>, M> List<M> convertToModelVersions(
            @NotNull final T dataAccessList,
            @NotNull final Supplier<M> modelSupplier
    ){

        // creates return list holding model reflections of data access objects
        final List<M> modelList = new ArrayList<M>();

        dataAccessList.forEach(
                (dataAccess) -> modelList.add(this.convertToModelVersion(dataAccess, modelSupplier))
        );

        return modelList;
    }

    /**
     * todo: nick add doc
     * @param dataAccess
     * @param modelSupplier
     * @param <T>
     * @param <M>
     * @return
     */
    public <T extends IDataAccess<M>, M> M convertToModelVersion(
            @NotNull final T dataAccess,
            @NotNull final Supplier<M> modelSupplier
    ){
        return dataAccess.convertTo(modelSupplier);
    }

}
