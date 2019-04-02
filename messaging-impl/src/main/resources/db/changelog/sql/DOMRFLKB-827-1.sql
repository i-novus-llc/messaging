update public.component set name = 'Оплата' where id = 1;

create table public.system_object_type(
  code varchar primary key,
  name varchar
);

insert into public.system_object_type values ('LOAN_SELECTION_FILE', 'Файл выборки кредитных средств');
insert into public.system_object_type values ('SURETY_CONTRACT', 'Договор поручительства');

alter table public.message add column notification_type varchar;
alter table public.message add column object_id varchar;
alter table public.message add column object_type varchar;
alter table public.message add constraint fk_message_system_object_type foreign key (object_type) references public.system_object_type(code);
