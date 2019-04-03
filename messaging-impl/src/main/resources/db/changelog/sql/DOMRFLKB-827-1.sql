update public.component set name = 'Оплата' where id = 1;

alter table public.message add column notification_type varchar;
alter table public.message add column object_id varchar;
alter table public.message add column object_type varchar;
