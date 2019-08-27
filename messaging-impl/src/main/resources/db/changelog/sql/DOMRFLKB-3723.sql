alter table message_setting add column code varchar;

comment on column message_setting.code is 'Код шаблона сообщения';