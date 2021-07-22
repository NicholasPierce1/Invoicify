package com.galvanize.invoicify.repository.repositories.invoicerepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.invoicify.repository.dataaccess.InvoiceDataAccess;
import com.galvanize.invoicify.repository.repositories.sharedfiles.DataAccessConversionHelper;
import com.galvanize.invoicify.repository.repositories.sharedfiles.QueryTableNameModifier;
import com.sun.istack.NotNull;
import org.h2.util.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Parameter;
import javax.persistence.Query;
import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class InvoiceRepositoryImpl implements InvoiceRepositoryCustom {

    private final EntityManagerFactory _entityManagerFactory;

    private final DataSource _dataSource;

    private final ObjectMapper _objectMapper;

    private final DateTimeFormatter _dateTimeFormatter;

    private final DataAccessConversionHelper _dataAccessConversionHelper;

    private final InvoiceRepositoryManagerHelper _invoiceRepositoryManagerHelper;

    @Autowired
    public InvoiceRepositoryImpl(
            EntityManagerFactory entityManagerFactory,
            DataSource dataSource,
            ObjectMapper objectMapper,
            DateTimeFormatter _dateTimeFormatter,
            DataAccessConversionHelper dataAccessConversionHelper) {
        this._entityManagerFactory = entityManagerFactory;
        this._dataSource = dataSource;
        this._objectMapper = objectMapper;
        this._dateTimeFormatter = _dateTimeFormatter;
        this._dataAccessConversionHelper = dataAccessConversionHelper;
        this._invoiceRepositoryManagerHelper = new InvoiceRepositoryManagerHelper(
                this._objectMapper,
                this._dateTimeFormatter,
                this._dataAccessConversionHelper
        );
    }

    @Override
    public InvoiceDataAccess fetchInvoice(long invoiceId, List<Long> recordIds) {
        return fetchInvoices(invoiceId, recordIds).get(0);
    }

    @Override
    public List<InvoiceDataAccess> fetchInvoices(long invoiceId, List<Long> recordIds) {
        EntityManager em = _entityManagerFactory.createEntityManager();
        //String recordIdsStr = recordIds.stream().map(String::valueOf).collect(Collectors.joining(","));
        
        String recordIdsStr = "";

        for (int i = 0; i < recordIds.size(); i++) {
            final String or = " or ";
            String placeHolder = "t1.id = %d";
            if(i != recordIds.size() -1) {// not last element
                placeHolder = placeHolder.concat(or);
            }
            recordIdsStr = recordIdsStr.concat(String.format(placeHolder, recordIds.get(i)));
        }

        String flatFeeBillingRecordWhereClause = QueryTableNameModifier.insertTableNamesIntoQuery(recordIdsStr, "f");
        String rateBasedBillingRecordWhereClause = QueryTableNameModifier.insertTableNamesIntoQuery(recordIdsStr, "r");

        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            final Connection connection = this._dataSource.getConnection();
            final Statement statement = connection.createStatement();

            String invoiceQueryStr = getInvoiceSelectQuery();
            invoiceQueryStr = invoiceQueryStr.replace("f_ids",flatFeeBillingRecordWhereClause);
            invoiceQueryStr = invoiceQueryStr.replace("r_ids",rateBasedBillingRecordWhereClause);

            System.out.println("\n\n\n" + invoiceQueryStr + "\n\n\n");

            //Query invoiceQuery = em.createNativeQuery(invoiceQueryStr);
            // invoiceQuery.setParameter(1, invoiceId);
            // invoiceQuery.setParameter(2, invoiceId);

           // List<Object[]> invoices = invoiceQuery.getResultList();
            List<InvoiceDataAccess> invoiceResultList = new ArrayList<InvoiceDataAccess>();

            final ResultSet resultSet = statement.executeQuery(invoiceQueryStr);

            // todo: invoke helper method for parsing & rendering InvoiceDataAccess
            // todo: then, refactor data access conversion helper



//            invoices.forEach(
//                    (objects ->
//                    {
//                        for (final Object object : objects)
//                            System.out.println(object);
//                    }
//                    )
//            );

            //DataAccessConversionHelper dataAccessConversionHelper = new DataAccessConversionHelper();
            //dataAccessConversionHelper.createDataAccessObjects(invoices,invoiceResultList, InvoiceDataAccess::new);

            System.out.println(invoiceResultList);
            return invoiceResultList;
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
            return new ArrayList<>();
        }
    }

    private String getInvoiceSelectQuery() {
        StringBuffer stringBuffer = new StringBuffer(
                  "select c_i.*, unionTable.* " +
                  "from " +
                  "( select a.*, c.*, i.* " +
                  "from app_user a, company c, invoice i " +
                  "where i.invoice_company_id = c.company_id " +
                  "and i.created_by = a.user_id) as c_i, " +
                  "( " +
                  " (select a.*, i.*, users.user_id as prefix_user_id, users.password as prefix_password, users.username as prefix_username, c.*, r.id, r.billing_record_company_id, r.billing_record_created_by, r.description, r.in_use, null as amount, r.quantity, r.rate " +
                  " from invoice_line_item i, rate_based_billing_record r, company c, app_user a, " +
                  " (select * from app_user) as users " +
                  " where i.billing_record_id = r.id " +
                  " and (r_ids) " +
                  " and r.billing_record_company_id = c.company_id " +
                  " and i.created_by = a.user_id " +
                  " and users.user_id = r.billing_record_created_by) " +
                  "union " +
                  " (select a.*, i.*, users.user_id as prefix_user_id, users.password as prefix_password, users.username as prefix_username, c.*, f.*, null as quantity, null as rate " +
                  " from invoice_line_item i, flat_fee_billing_record f, company c, app_user a, " +
                  " (select * from app_user) as users " +
                  " where i.billing_record_id = f.id " +
                  " and (f_ids)" +
                  " and f.billing_record_company_id = c.company_id " +
                  " and i.created_by = a.user_id " +
                  " and users.user_id = f.billing_record_created_by) " +
                  ") as unionTable " +
                  "where " +
                  "c_i.invoice_id = unionTable.invoice_id;");
        return stringBuffer.toString();
    }

    private class InvoiceRepositoryManagerHelper{

        private final ObjectMapper _objectMapper;

        private final DateTimeFormatter _dateTimeFormatter;

        private final DataAccessConversionHelper _dataAccessConversionHelper;

        // enumerates static final members representing the composite (children) hashmaps

        // enumerates static final fields for the key-values for composite (children) hashmaps

        // private static final String INVOICE_

        public InvoiceRepositoryManagerHelper(
                ObjectMapper objectMapper,
                DateTimeFormatter dateTimeFormatter,
                DataAccessConversionHelper dataAccessConversionHelper
        ){
            this._objectMapper = objectMapper;
            this._dateTimeFormatter = dateTimeFormatter;
            this._dataAccessConversionHelper = dataAccessConversionHelper;
        }

        public @NotNull List<HashMap<String, Object>> createDeserializableInvoicesFromResultSet(
                @NotNull final ResultSet resultSet) throws SQLException {

            boolean endOfRows = resultSet.isLast();

            final List<HashMap<String, Object>> invoiceDeserializableList = new ArrayList<HashMap<String, Object>>();

            while (!endOfRows)
            {

                // holds a

                resultSet.next();
                //System.out.println(resultSet.getMetaData().getColumnCount());
                System.out.println(resultSet.getMetaData().getColumnCount());
                for (int column = 1; column < resultSet.getMetaData().getColumnCount() - 1; column++)
                {

                    // extract column value from result set
                    final Object columnValue = resultSet.getObject(column);

                    // extracts column name from result set
                    String columnName = resultSet.getMetaData().getColumnName(column);

                    // if columnName starts with 'Prefix' then remove sub-query prefix from name
                    if(columnName.startsWith("PREFIX"))
                        columnName = this._dataAccessConversionHelper.removeSubQueryPrefixFromColumnName(columnName);

                    // sets key - value pair

                }

                if(resultSet.isLast())
                    endOfRows = true;
            }


            return null;
        }

    }

}
