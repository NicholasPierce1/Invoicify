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

select c_i.*, unionTable.*
from
( select a.*, c.*, i.*
     from app_user a, company c, invoice i
     where i.invoice_company_id = c.company_id
     and i.created_by = a.user_id) as c_i,
(
     (select a.*, i.*, users.user_id as prefix_user_id, users.password as prefix_password, users.username as prefix_username, c.*, r.id, r.billing_record_company_id, r.billing_record_created_by, r.description, r.in_use, null as amount, r.quantity, r.rate
           from invoice_line_item i, rate_based_billing_record r, company c, app_user a,
           (select * from app_user) as users
     where i.billing_record_id = r.id
     and r.billing_record_company_id = c.company_id
     and i.created_by = a.user_id
     and users.user_id = r.billing_record_created_by)
union
     (select a.*, i.*, users.user_id as prefix_user_id, users.password as prefix_password, users.username as prefix_username, c.*, f.*, null as quantity, null as rate
           from invoice_line_item i, flat_fee_billing_record f, company c, app_user a,
           (select * from app_user) as users
     where i.billing_record_id = f.id
     and f.billing_record_company_id = c.company_id
     and i.created_by = a.user_id
     and users.user_id = f.billing_record_created_by)
) as unionTable
where
c_i.invoice_id = unionTable.invoice_id;
