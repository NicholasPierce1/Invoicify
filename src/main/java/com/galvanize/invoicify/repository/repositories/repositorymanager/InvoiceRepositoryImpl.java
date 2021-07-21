package com.galvanize.invoicify.repository.repositories.repositorymanager;

import com.galvanize.invoicify.repository.repositories.sharedfiles.QueryTableNameModifier;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.lang.annotation.Annotation;
import java.util.List;

public class InvoiceRepositoryImpl implements InvoiceRepositoryCustom {

    private final EntityManagerFactory _entityManagerFactory;
    private final String INVOICE_TABLE = "invoice";
    private final String INVOICE_LINE_ITEM_TABLE = "invoice_line_item";
    private final String USER_TABLE = "app_user";
    private final String FLAT_FEE_BILLING_RECORD_TABLE = "flat_fee_billing_record";
    private final String RATE_BASE_BILLING_RECORD_TABLE = "rate_base_billing_record";


    @Autowired
    public InvoiceRepositoryImpl(EntityManagerFactory entityManagerFactory) {
        _entityManagerFactory = entityManagerFactory;
    }

    public void findInvoice(String invoiceId, List<Long> recordIds) {
        EntityManager em = _entityManagerFactory.createEntityManager();
        String recordIdsStr = String.join(", ", recordIds.toString());

        String invoiceQueryStr = getInvoiceSelectQuery();
        Query invoiceQuery = em.createNativeQuery(invoiceQueryStr);
        invoiceQuery.setParameter(1, recordIdsStr);
        invoiceQuery.setParameter(2, recordIdsStr);
        invoiceQuery.setParameter(3, invoiceId);

        List<Object[]> invoices = invoiceQuery.getResultList();
        System.out.println(invoices);






    }

    private String getInvoiceSelectQuery() {
        StringBuffer stringBuffer = new StringBuffer(" select\n" +
                "     i.id,\n" +
                "     (select id from company where id = i.company_id ) company_id,\n" +
                "     (select name from company where id = i.company_id) company_name,\n" +
                "     i.created_on  invoice_created_on,\n" +
                "     (select id from app_user where id = i.created_by) invoice_user_id,\n" +
                "     (select username from app_user where id = i.created_by ) invoice_user_created_by,\n" +
                "     (select password from app_user where id = i.created_by ) invoice_user_password,\n" +
                "     i.description,\n" +
                "     line_items.*\n" +
                "  from invoice i, (\n" +
                "  with flat_billing_records as (\n" +
                "     select f.id ,\n" +
                "               f.created_by,\n" +
                "               u.username,\n" +
                "               u.password\n" +
                "       from flat_fee_billing_Record f join app_user u on (f.created_by = u.id)\n" +
                "       where f.id in (?)\n" +
                "   ), rate_based_billing_records as (\n" +
                "      select r.id,\n" +
                "                r.created_by,\n" +
                "               u.username,\n" +
                "               u.password\n" +
                "       from rate_based_billing_record r join app_user u on (r.created_by = u.id)\n" +
                "       where r.id in (?)\n" +
                "   )\n" +
                "  select * from flat_billing_records\n" +
                "  union all\n" +
                "  select * from rate_based_billing_records\n" +
                "  ) line_items"+
                " where (i.id = ? or ? is null);");
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
