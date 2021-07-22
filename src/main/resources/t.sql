  select
      i.id,
      (select company.id from company where id = i.company_id ) company_id,
      (select company.name from company where id = i.company_id) company_name,
      i.created_on as invoice_created_on,
      (select app_user.id from app_user where id = i.created_by) invoice_user_id,
      (select app_user.username from app_user where id = i.created_by ) invoice_user_created_by,
      (select app_user.password from app_user where id = i.created_by ) invoice_user_password,
      i.description,
      line_items.*
   from invoice i, (
   with flat_billing_records as (
      select f.id ,
                f.created_by,
                u.username,
                u.password
        from flat_fee_billing_Record f join app_user u on (f.created_by = u.id) join invoice_line_item ili
        where f.id in (1,2)
    ), rate_based_billing_records as (
       select r.id,
                 r.created_by,
                u.username,
                u.password
        from rate_based_billing_record r join app_user u on (r.created_by = u.id)
        where r.id in (4,5)
    )
   select * from flat_billing_records
   union all
   select * from rate_based_billing_records
   ) line_items
   where i.id = 1 -- add (or ? is null)
   and exists (select 1 from invoice_line_item ili where ili.invoice_id = i.id and billing_record_id = line_items.id);


