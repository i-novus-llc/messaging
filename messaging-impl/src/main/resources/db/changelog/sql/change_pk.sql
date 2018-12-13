alter table message drop constraint message_pkey;
alter table message add constraint message_pk primary key(id,recipient,system_id);
