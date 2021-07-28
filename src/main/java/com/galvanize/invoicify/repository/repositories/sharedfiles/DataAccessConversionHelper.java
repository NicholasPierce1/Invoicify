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
 * <h2>DataAccessConversionHelper</h2>
 *
 * <p>
 * Implementing Helper methods to create data access objects(DAOs)
 * from the data from the database
 * </p>
 */
@Component
public final class DataAccessConversionHelper {


    @Autowired
    ObjectMapper objectMapper;

    /**
     *<p>
     *  Creates a List of the targeted DataAccess object from a List of Map representation/reflections of
     *  Spring Data's Result Set Graph.
     *</p>
     * @param data List of the conglomerated map-data representing the targeted DataAccess returned from the ResultSet
     * @param serializerEndpoint class definition of the targeted DataAccess type
     * @param <T> DataAccess type to convert the conglomerated-maps to
     * @return List of the targeted DataAccess types
 */
    public < T extends IDataAccess<?>> List<T> createDataAccessObjects(
            @NotNull final List<? extends Map<String, ?>> data,
            @NotNull final Class<T> serializerEndpoint)
            throws DataAccessConversionException, IllegalArgumentException {

        // initializes a list to retain outputted DAOs from ORM deserialization
        final List<T> dataAccessObjects = new ArrayList<T>();

        // making sure data targeted for ORM deserialization is non-empty
        if(data.size() == 0)
            throw new IllegalArgumentException("list size of data access must not be 0");
        // loops through data and adds each DAO created to the list
        try{
            for (final Map<String, ? > dataAccess : data) {
                dataAccessObjects.add(this.createDataAccessObject(dataAccess, serializerEndpoint));
            }

            return dataAccessObjects;
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
     *  Creates a DataAccess object from a Map representation/reflection of
     *  Spring Data's Result Set Graph.
     *</p>
     * @param data Conglomerated map-data representing the targeted DataAccess returned from the ResultSet
     * @param serializerEndpoint class definition of the targeted DataAccess type
     * @param <T> DataAccess type to convert the conglomerated-map to
     * @return the targeted DataAccess type
     */
    public <T extends IDataAccess<?>> T createDataAccessObject(
            @NotNull final Map<String, ?> data,
            @NotNull final Class<T> serializerEndpoint)
            throws DataAccessConversionException, IllegalArgumentException {
        // takes the specified type and then updates the DAO with the data
        return this.objectMapper.convertValue(
                data,
                serializerEndpoint
        );
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

        if(!columnWithPrefix.startsWith("prefix"))
            throw new IllegalArgumentException("column name " + columnWithPrefix + " does not start with" +
                    "'PREFIX'. Are you sure this is a sub-query column?");

        return columnWithPrefix.replaceFirst("prefix[0-9]*_", "");

    }

}
