alter table public.message add column if not exists notification_type varchar;
alter table public.message add column if not exists object_id varchar;
alter table public.message add column if not exists object_type varchar;
