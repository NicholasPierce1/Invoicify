package com.galvanize.invoicify.repository.repositories.sharedfiles;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.invoicify.repository.dataaccess.definition.IDataAccess;
import com.sun.istack.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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


    @Autowired
    ObjectMapper objectMapper;

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
     * <p>
     *      Converts a list of data access objects to their respective model
     *      reflections.
     * </p>
     * @param dataAccessList: List retaining data access object that'll be used to create the list
     *                      of model reflections
     * @param modelSupplier: provides implementation of creating a model object.
     *                     NOTE: the default state set in the supplier may/will be written over.
     * @param <T>: List of IDataAccess objects with their respective model type
     * @param <K>: An IDataAccess object with its respective model type
     * @param <M>: A model type used to create data access reflections
     * @return a list of model objects
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
     * <p>
     *     Converts a data access object to a model object
     * </p>
     * @param dataAccess: data access object that'll be used to create the list
     *                     of model reflections
     * @param modelSupplier: provides implementation of creating a model object.
     *                    NOTE: the default state set in the supplier may/will be written over.
     * @param <T>: An IDataAccess object with its respective model type
     * @param <M>: A model type used to create data access reflections
     * @return a model object of the reflect data access definition
     */
    public <T extends IDataAccess<M>, M> M convertToModelVersion(
            @NotNull final T dataAccess,
            @NotNull final Supplier<M> modelSupplier
    ){
        return dataAccess.convertToModel(modelSupplier);
    }


    //todo: add documentation
    public @NotNull String removeSubQueryPrefixFromColumnName(
            @NotNull final String columnWithPrefix
    ){

        if(!columnWithPrefix.startsWith("PREFIX"))
            throw new IllegalArgumentException("column name " + columnWithPrefix + " does not start with" +
                    "'PREFIX'. Are you sure this is a sub-query column?");

        return columnWithPrefix.replaceFirst("PREFIX[0-9]*_", "");

    }

}
