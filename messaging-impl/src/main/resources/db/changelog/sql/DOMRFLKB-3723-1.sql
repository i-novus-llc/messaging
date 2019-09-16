alter table public.message_setting add column if not exists code varchar not null;

comment on column message_setting.code is 'Код шаблона сообщения';

create unique index if not exists code_ux on message_setting (code);