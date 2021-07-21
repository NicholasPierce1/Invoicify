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
        invoiceQueryStr = invoiceQueryStr.replace("f_ids",flatFeeBillingRecordWhereClause);
        invoiceQueryStr = invoiceQueryStr.replace("r_ids",rateBasedBillingRecordWhereClause);

        System.out.println(invoiceQueryStr);

        Query invoiceQuery = em.createNativeQuery(invoiceQueryStr);
        invoiceQuery.setParameter(1, invoiceId);
        invoiceQuery.setParameter(2, invoiceId);


        List<Object[]> invoices = invoiceQuery.getResultList();
        List<InvoiceDataAccess> invoiceResultList = new ArrayList<InvoiceDataAccess>();

        System.out.println(invoices);

        DataAccessConversionHelper dataAccessConversionHelper = new DataAccessConversionHelper();
        dataAccessConversionHelper.createDataAccessObjects(invoices,invoiceResultList, InvoiceDataAccess::new);

        System.out.println(invoiceResultList);
        return invoiceResultList;
    }

    private String getInvoiceSelectQuery() {
        StringBuffer stringBuffer = new StringBuffer(" select  " +
                "     i.id," +
                "     (select id from company where id = i.company_id ) company_id,  " +
                "     (select name from company where id = i.company_id) company_name,  " +
                "     i.created_on  invoice_created_on,  " +
                "     (select id from app_user where id = i.created_by) invoice_user_id,  " +
                "     (select username from app_user where id = i.created_by ) invoice_user_created_by,  " +
                "     (select password from app_user where id = i.created_by ) invoice_user_password,  " +
                "     i.description,  " +
                "     line_items.*  " +
                "  from invoice i, (  " +
                "  with flat_billing_records as (  " +
                "     select f.id ,  " +
                "               f.created_by,  " +
                "               u.username,  " +
                "               u.password  " +
                "       from flat_fee_billing_Record f join app_user u on (f.created_by = u.id)  " +
                "       where f_ids  " +
                "   ), rate_based_billing_records as (  " +
                "      select r.id,  " +
                "                r.created_by,  " +
                "               u.username,  " +
                "               u.password  " +
                "       from rate_based_billing_record r join app_user u on (r.created_by = u.id)  " +
                "       where r_ids  " +
                "   )  " +
                "  select * from flat_billing_records  " +
                "  union all  " +
                "  select * from rate_based_billing_records  " +
                "  ) line_items "+
                " where (i.id = ? or ? is null)" +
                "    and exists (select 1 from invoice_line_item ili where ili.invoice_id = i.id and billing_record_id = line_items.id);");
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
