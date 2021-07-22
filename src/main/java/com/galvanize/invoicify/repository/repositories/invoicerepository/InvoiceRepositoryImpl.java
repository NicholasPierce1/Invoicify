package com.galvanize.invoicify.repository.repositories.invoicerepository;

import com.galvanize.invoicify.repository.dataaccess.InvoiceDataAccess;
import com.galvanize.invoicify.repository.repositories.sharedfiles.DataAccessConversionHelper;
import com.galvanize.invoicify.repository.repositories.sharedfiles.QueryTableNameModifier;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Parameter;
import javax.persistence.Query;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InvoiceRepositoryImpl implements InvoiceRepositoryCustom {

    private final EntityManagerFactory _entityManagerFactory;


    @Autowired
    public InvoiceRepositoryImpl(EntityManagerFactory entityManagerFactory) {
        _entityManagerFactory = entityManagerFactory;
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



        String invoiceQueryStr = getInvoiceSelectQuery();
        // invoiceQueryStr = invoiceQueryStr.replace("f_ids",flatFeeBillingRecordWhereClause);
        // invoiceQueryStr = invoiceQueryStr.replace("r_ids",rateBasedBillingRecordWhereClause);

        System.out.println("\n\n\n" + invoiceQueryStr + "\n\n\n");

        Query invoiceQuery = em.createNativeQuery(invoiceQueryStr);
        // invoiceQuery.setParameter(1, invoiceId);
        // invoiceQuery.setParameter(2, invoiceId);

        List<Object[]> invoices = invoiceQuery.getResultList();
        List<InvoiceDataAccess> invoiceResultList = new ArrayList<InvoiceDataAccess>();

        invoices.forEach(
                (objects ->
                    {
                        for (final Object object : objects)
                            System.out.println(object);
                    }
                )
        );

        //DataAccessConversionHelper dataAccessConversionHelper = new DataAccessConversionHelper();
        //dataAccessConversionHelper.createDataAccessObjects(invoices,invoiceResultList, InvoiceDataAccess::new);

        System.out.println(invoiceResultList);
        return invoiceResultList;
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
                  " (select a.*, i.*, users.user_id as rate_user, users.password as rate_pwd, users.username as rate_userName, c.*, r.id, r.billing_record_company_id, r.billing_record_created_by, r.description, r.in_use, null as amount, r.quantity, r.rate " +
                  " from invoice_line_item i, rate_based_billing_record r, company c, app_user a, " +
                  " (select * from app_user) as users " +
                  " where i.billing_record_id = r.id " +
                  " and r.billing_record_company_id = c.company_id " +
                  " and i.created_by = a.user_id " +
                  " and users.user_id = r.billing_record_created_by) " +
                  "union " +
                  " (select a.*, i.*, users.user_id as rate_user, users.password as rate_pwd, users.username as rate_userName, c.*, f.*, null as quantity, null as rate " +
                  " from invoice_line_item i, flat_fee_billing_record f, company c, app_user a, " +
                  " (select * from app_user) as users " +
                  " where i.billing_record_id = f.id " +
                  " and f.billing_record_company_id = c.company_id " +
                  " and i.created_by = a.user_id " +
                  " and users.user_id = f.billing_record_created_by) " +
                  ") as unionTable " +
                  "where " +
                  "c_i.invoice_id = unionTable.invoice_id;");
        return stringBuffer.toString();
    }

    @Override
    public String value() {
        return null;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }
}
