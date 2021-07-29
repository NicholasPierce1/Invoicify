package com.galvanize.invoicify.repository.dataaccess;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.galvanize.invoicify.models.Company;
import com.galvanize.invoicify.repository.dataaccess.definition.IDataAccess;
import javax.persistence.*;
import java.util.function.Supplier;

/**
 * <h2>
 *     CompanyDataAccess
 * </h2>
 * <p>
 *     Spring bean Entity that manages the connection between the database and the Model. It  corresponds to the company
 *     table in the database. It has fields that expresses the columns in the table directly. It implements IDataAccess
 *     interface and inherits the methods: createDataAccess, convertToModel, convertToDataAccess; all of which wraps and
 *     unwraps the Company Model while restricting transactions to the database.
 * </p>
 */
@Entity
@Table(name = "company")
public final class CompanyDataAccess implements IDataAccess<Company> {

    //fields
    /**
     * <p>
     *     Autogenerated serialization id when persisted to the database.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_id")
    @JsonProperty(value = "company_id")
    public Long id;

    /**
     * <p>
     *     String name that is stored in the non-nullable company_name column of the Company table.
     * </p>
     */
    @Column(nullable = false)
    public String name;

    // constructor

    public CompanyDataAccess(){}

    /**
     * <p>
     *     This constructor takes in user input (String name) as well as an auto-generated id (Long id). It cannot have
     *     a one arg constructor because the company_name column in the table cannot have null entries, therefore, there
     *     can only be a no args or all arg constructor.
     * </p>
     * @param id : Auto-serialized number when created and saved
     * @param name : The String name of the company assigned by user when creating company
     */
    public CompanyDataAccess(Long id, String name) {
        this.id=id;
        this.name=name;
    }


    //set & get

    /**
     * <p>
     *     This gets the company id in the company table.
     * </p>
     * @return id : id stored in the company_id field in Company table
     */
    public Long getId() {
        return id;
    }

    /**
     * <p>
     *     This gets the company name that is stored in the Company table.
     * </p>
     * @return name : retrieves the name that was assigned to that company.
     */
    public String getName() {
        return name;
    }

    /**
     * <p>
     *     This sets the serialized id that was assigned to the Company DataAccessObject.
     * </p>
     * @param id : takes in the serialized id that was assigned to the Company DataAccessObject.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * <p>
     *     This sets the String name for the Company DataAccessObject that the user assigns.
     * </p>
     * @param name : takes in the String name for the Company DataAccessObject that the user assigns.
     */
    public void setName(String name) {
        this.name = name;
    }

    // methods

    /**
     * <p>
     *     The method that creates the DataAccessObject that communicates with the database.
     * </p>
     * @param supplier : takes in the DataAccessObject that Spring auto-generates.
     */
    @Override
    public <M extends Company> M convertToModel(Supplier<M> supplier) {

        final M company = supplier.get();

        company.setId(this.getId());
        company.setName(this.getName());

        return company;

    }

    /**
     * <p>
     *     Converts a Model Object to a DataAccessObject and sets the name and id.
     * </p>
     * @param M : A Model Object used to convert DataAccessObject
     */
    @Override
    public <M extends Company> void convertToDataAccess(M modelObject) {
        this.setId(modelObject.getId());
        this.setName(modelObject.getName());
    }

    /**
     * <p>
     *      Overrides Object's equal method to assert equality amongst CompanyDataAccess objects.
     *      If ID is set in the CompanyDataAccess input then equality of state
     *      (positing that the addresses are not equal) will be asserted as well.
     * </p>
     * @param a: a nullable CompanyDataAccess object
     * @return boolean based on the equality of the memory addresses or the state of objects.
     */
    @Override
    public boolean equals(Object a){

        if(a == this)
            return true;

        if(a == null)
            return false;

        if(!(a instanceof CompanyDataAccess))
            return false;

        final CompanyDataAccess companyDataAccess = (CompanyDataAccess)a;

        final boolean baseCondition =
                companyDataAccess.getName().equals(this.getName());

        return companyDataAccess.getId() == null ?
                baseCondition
                :
                baseCondition && companyDataAccess.getId().equals(this.getId());

    }

}
