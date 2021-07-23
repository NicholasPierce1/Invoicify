package com.galvanize.invoicify.repository.repositories.invoicerepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.invoicify.repository.dataaccess.BillingRecordDataAccess;
import com.galvanize.invoicify.repository.dataaccess.InvoiceDataAccess;
import com.galvanize.invoicify.repository.dataaccess.InvoiceLineItemDataAccess;
import com.galvanize.invoicify.repository.repositories.sharedfiles.DataAccessConversionHelper;
import com.galvanize.invoicify.repository.repositories.sharedfiles.QueryTableNameModifier;
import com.sun.istack.NotNull;
import org.h2.util.json.JSONObject;
import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.javatuples.Triplet;
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
import java.util.*;
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

        String invoiceQueryStr = "";

        if (recordIds != null && recordIds.size() > 0) {
            String recordIdsStr = " and (";

            for (int i = 0; i < recordIds.size(); i++) {
                final String or = " or ";
                String placeHolder = "t1.id = %d";
                if (i != recordIds.size() - 1) {// not last element
                    placeHolder = placeHolder.concat(or);
                }
                recordIdsStr = recordIdsStr.concat(String.format(placeHolder, recordIds.get(i)));
            }
            recordIdsStr += ")";

            String flatFeeBillingRecordWhereClause = QueryTableNameModifier.insertTableNamesIntoQuery(recordIdsStr, "f");
            String rateBasedBillingRecordWhereClause = QueryTableNameModifier.insertTableNamesIntoQuery(recordIdsStr, "r");
            String invoiceAndClause = " and c_i.invoice_id = " + invoiceId;

            invoiceQueryStr = getInvoiceSelectQuery(rateBasedBillingRecordWhereClause, flatFeeBillingRecordWhereClause, invoiceAndClause);
        } else {
            invoiceQueryStr = getInvoiceSelectQuery("","","");
        }

        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            final Connection connection = this._dataSource.getConnection();
            final Statement statement = connection.createStatement();

            final ResultSet resultSet = statement.executeQuery(invoiceQueryStr);
            final List<? extends Map<String, ?>> invoices = this._invoiceRepositoryManagerHelper.createDeserializableInvoicesFromResultSet(resultSet);

            final List<InvoiceDataAccess> invoiceDataAccessList =
                    this._dataAccessConversionHelper.createDataAccessObjects(
                            invoices,
                            InvoiceDataAccess.class
                    );

            System.out.println("we made it out bois!");

            return invoiceDataAccessList;
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
            return new ArrayList<>();
        }
    }


    private String getInvoiceSelectQuery(String rIds, String fIds, String invId) {
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
                  " where i.billing_record_id = r.id " + rIds+
                  " and r.billing_record_company_id = c.company_id " +
                  " and i.created_by = a.user_id " +
                  " and users.user_id = r.billing_record_created_by) " +
                  "union " +
                  " (select a.*, i.*, users.user_id as prefix_user_id, users.password as prefix_password, users.username as prefix_username, c.*, f.*, null as quantity, null as rate " +
                  " from invoice_line_item i, flat_fee_billing_record f, company c, app_user a, " +
                  " (select * from app_user) as users " +
                  " where i.billing_record_id = f.id " +
                  fIds +
                  " and f.billing_record_company_id = c.company_id " +
                  " and i.created_by = a.user_id " +
                  " and users.user_id = f.billing_record_created_by) " +
                  ") as unionTable " +
                  "where " +
                  "c_i.invoice_id = unionTable.invoice_id " +
                           invId   + ";");
        return stringBuffer.toString();
    }

    private static class InvoiceRepositoryManagerHelper{

        private final ObjectMapper _objectMapper;

        private final DateTimeFormatter _dateTimeFormatter;

        private final DataAccessConversionHelper _dataAccessConversionHelper;

        // enumerates static final fields for the key-values for composite (children) hashmaps

        // invoice
        private static final String INVOICE_COMPANY = "company";
        private static final String INVOICE_COMPANY_KEY = "INVOICE_COMPANY";
        private static final String INVOICE_USER = "user";
        private static final String INVOICE_USER_KEY = "INVOICE_USER";

        // invoice line item
        private static final String IL_USER = "user";
        private static final String IL_USER_KEY = "IL_USER";
        private static final String IL_BILLING_RECORD = "billingRecord";
        private static final String IL_BILLING_RECORD_KEY = "IL_BILLING_RECORD";

        // billing record
        private static final String BR_USER = "user";
        private static final String BR_USER_KEY = "BR_USER";
        private static final String BR_COMPANY = "company";
        private static final String BR_COMPANY_KEY = "BR_COMPANY";

        // holds all invoice line items according to their invoice id (parent)
        private final Map<String, List<Map<String, ?>>> invoiceLineItemsPerInvoice =
                new HashMap<>();

        private static final String INVOICE_LINE_ITEM = "lineItems";

        // enumerates keys for parent hash map keys
        private static final String INVOICE_KEY = "INVOICE_KEY";
        private static final String INVOICE_LINE_ITEM_KEY = "INVOICE_LINE_ITEM_KEY";
        private static final String BR_KEY = "BR_KEY";

        // key to extract invoice id from an invoice
        private static final String INVOICE_ID_KEY = "invoice_id";


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

                resultSet.next();

                Map<String, HashMap<String, Object>> childrenMaps = null;


                for (int column = 1; column < resultSet.getMetaData().getColumnCount() + 1; column++)
                {

                    // extract column value from result set
                    final Object columnValue = resultSet.getObject(column);

                    // extracts column name from result set
                    // sets to lowercase to match data access object's json property names
                    String columnName = resultSet.getMetaData().getColumnName(column).toLowerCase(Locale.ROOT);

                    // if columnName starts with 'Prefix' then remove sub-query prefix from name
                    if(columnName.startsWith("prefix"))
                        columnName = this._dataAccessConversionHelper.removeSubQueryPrefixFromColumnName(columnName);

                    // get all children maps if first column of row
                    if(column == 1)
                        childrenMaps = this.initializeChildrenHashMaps();

                    // get current map, boolean, map triplet
                    final Quartet<Map<String, Object>, Boolean, Map<String, Object>, String>
                            currentMapState = this.getCurrentHashMap(childrenMaps, column);

                    // integrity check (current map and boolean shall never be null)
                    if(currentMapState.getValue1() == null || currentMapState.getValue0() == null)
                        throw new RuntimeException("Null encountered on needed state for ORM parsing");
                    
                    // if boolean is true then set current map to the given parent map
                    // only IF parent map != null (parent's don't have a parent)
                    if(currentMapState.getValue1() && currentMapState.getValue2() != null) {

                        if(currentMapState.getValue3() == null)
                            throw new RuntimeException("Null encountered on needed state for ORM parsing of parent-child hookup");

                        // encapsulate child map into a list for invoice parent (data type: List<Map<String, ? extends Map<String, ?>>)
                        currentMapState.getValue2().put(
                                currentMapState.getValue3(),
                                currentMapState.getValue3().equals(INVOICE_LINE_ITEM) ?
                                        new ArrayList<Map<String, ?>>(){{
                                            add(currentMapState.getValue0());
                                        }}
                                        :
                                        currentMapState.getValue0()
                        );
                    }

                    // if column value is 'amount'
                    // determine if current billing record is flat fee or rate based
                    // amount == null then rate based
                    if(columnName.equals("amount")){
                        currentMapState.getValue0().put(
                                "type",
                                columnValue == null ?
                                        BillingRecordDataAccess.SubTypeTable.RateBased.getTypeName()
                                        :
                                        BillingRecordDataAccess.SubTypeTable.FlatFee.getTypeName()
                        );
                    }

                    // sets key - value pair to current map
                    currentMapState.getValue0().put(columnName, columnValue);

                    // if last column in row then add invoice map (with all children attached) to result list
                    if(column == resultSet.getMetaData().getColumnCount() - 1) {
                        invoiceDeserializableList.add(
                                childrenMaps.get(INVOICE_KEY)
                        );
                    }

                }

                if(resultSet.isLast())
                    endOfRows = true;
            }

            // holds dynamic maps for each invoice (key value is its ID)
            // used to extract list of invoice line items and add a new one when id values match
            final Map<Long, HashMap<String, Object>> invoices = new HashMap<Long, HashMap<String, Object>>();


            // iterates over all maps and inserts into invoices if the id doesn't exist yet
            for(final HashMap<String, Object> invoice : invoiceDeserializableList){
                // checks if id values match (same invoice -- line item different though)
                if(invoices.containsKey((Long)invoice.get(INVOICE_ID_KEY))) { // exist already add invoice line item
                    ((List<Map<String, ?>>)
                            ((Map<String, Object>)invoices.get(
                                    (Long)invoice.get(INVOICE_ID_KEY)
                                )
                            )
                                    .get(INVOICE_LINE_ITEM)
                        )
                            .add(
                                    // safe conversion && will always hold only 1 item
                                    ((List<Map<String, ?>>) invoice.get(INVOICE_LINE_ITEM)).get(0)
                            );
                }
                else  // new invoice, add invoice to invoice map
                    invoices.put(
                            (Long) invoice.get(INVOICE_ID_KEY),
                            invoice
                    );

            }

            return new ArrayList<HashMap<String, Object>>(invoices.values());
        }

        // returns a hashmap holding all children hashmap
        private @NotNull Map<String, HashMap<String, Object>> initializeChildrenHashMaps(){

            // creates return map
            final Map<String, HashMap<String, Object>> childrenMaps = new HashMap<String, HashMap<String, Object>>();

            // enumerates static final members representing the composite (children) hashmaps
            final HashMap<String,Object> invoiceCompany = new HashMap<String, Object>();
            final HashMap<String,Object> invoiceUser = new HashMap<String, Object>();

            final HashMap<String,Object> il_user = new HashMap<String, Object>();
            final HashMap<String,Object> il_br = new HashMap<String, Object>();

            final HashMap<String,Object> br_user = new HashMap<String, Object>();
            final HashMap<String,Object> br_company = new HashMap<String, Object>();

            // creates the parent maps
            final HashMap<String,Object> invoice = new HashMap<>();
            final HashMap<String,Object> invoice_lineItem = new HashMap<>();
            final HashMap<String,Object> billingRecord = new HashMap<>();
            
            // sets children maps each composite map
            childrenMaps.put(INVOICE_COMPANY_KEY, invoiceCompany);
            childrenMaps.put(INVOICE_USER_KEY, invoiceUser);
            childrenMaps.put(IL_BILLING_RECORD_KEY, il_br);
            childrenMaps.put(IL_USER_KEY, il_user);
            childrenMaps.put(BR_COMPANY_KEY, br_company);
            childrenMaps.put(BR_USER_KEY, br_user);
            childrenMaps.put(INVOICE_KEY, invoice);
            childrenMaps.put(INVOICE_LINE_ITEM_KEY, invoice_lineItem);
            childrenMaps.put(BR_KEY, billingRecord);


            return childrenMaps;
        }

        private @NotNull Quartet<Map<String, Object>, Boolean, Map<String, Object>, String> getCurrentHashMap(
                @NotNull Map<String, ? extends Map<String, Object>> childrenMaps,
                @NotNull final int index){

            if(index >= 1 && index <= 3)
                return new Quartet<Map<String, Object>, Boolean, Map<String, Object>, String>(
                        childrenMaps.get(INVOICE_USER_KEY),
                        index == 1,
                        childrenMaps.get(INVOICE_KEY),
                        INVOICE_USER
                );
            else if(index >= 4 && index <= 5)
                return new Quartet<Map<String, Object>, Boolean, Map<String, Object>, String>(
                        childrenMaps.get(INVOICE_COMPANY_KEY),
                        index == 4,
                        childrenMaps.get(INVOICE_KEY),
                        INVOICE_COMPANY
                );
            else if(index >= 6 && index <= 10)
                return new Quartet<Map<String, Object>, Boolean, Map<String, Object>, String>(
                        childrenMaps.get(INVOICE_KEY),
                        index == 6,
                        null,
                        null
                );
            else if(index >= 11 && index <= 13)
                return new Quartet<Map<String, Object>, Boolean, Map<String, Object>, String>(
                        childrenMaps.get(IL_USER_KEY),
                        index == 11,
                        childrenMaps.get(INVOICE_LINE_ITEM_KEY),
                        IL_USER
                );
            else if(index >= 14 && index <= 18)
                return new Quartet<Map<String, Object>, Boolean, Map<String, Object>, String>(
                        childrenMaps.get(INVOICE_LINE_ITEM_KEY),
                        index == 14,
                        childrenMaps.get(INVOICE_KEY),
                        INVOICE_LINE_ITEM
                );
            else if(index >= 19 && index <= 21)
                return new Quartet<Map<String, Object>, Boolean, Map<String, Object>, String>(
                        childrenMaps.get(BR_USER_KEY),
                        index == 19,
                        childrenMaps.get(BR_KEY),
                        BR_USER
                );
            else if(index >= 22 && index <= 23)
                return new Quartet<Map<String, Object>, Boolean, Map<String, Object>, String>(
                        childrenMaps.get(BR_COMPANY_KEY),
                        index == 22,
                        childrenMaps.get(BR_KEY),
                        BR_COMPANY
                );
            else if(index >= 24 && index <= 31)
                return new Quartet<Map<String, Object>, Boolean, Map<String, Object>, String>(
                        childrenMaps.get(BR_KEY),
                        index == 24,
                        childrenMaps.get(INVOICE_LINE_ITEM_KEY),
                        IL_BILLING_RECORD
                );

            return new Quartet<Map<String, Object>, Boolean, Map<String, Object>, String>(null, null, null, null); // will not be set

        }

    }

}
