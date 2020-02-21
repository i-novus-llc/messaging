ALTER TABLE public.recipient DROP CONSTRAINT recipient_message_id_fkey;
ALTER TABLE public.recipient ADD COLUMN message_id_old varchar;
UPDATE public.recipient SET message_id_old = message_id;
ALTER TABLE public.recipient ALTER COLUMN message_id drop not null;
UPDATE public.recipient SET message_id = NULL;
ALTER TABLE public.recipient ALTER COLUMN message_id TYPE uuid USING message_id::uuid;


ALTER TABLE public.message DROP CONSTRAINT message_pkey;
ALTER TABLE public.message ADD COLUMN id_old varchar;
UPDATE public.message SET id_old = id;
ALTER TABLE public.message ALTER COLUMN id drop not null;
UPDATE public.message SET id = NULL;
ALTER TABLE public.message ALTER COLUMN id TYPE uuid USING id::uuid;
ALTER TABLE public.message ALTER COLUMN id SET DEFAULT public.uuid_generate_v4();
UPDATE public.message SET id = public.uuid_generate_v4();
ALTER TABLE public.message ALTER COLUMN id SET NOT NULL;
ALTER TABLE public.message ADD PRIMARY KEY (id);

UPDATE public.recipient SET message_id = public.message.id FROM public.message WHERE public.message.id_old = public.recipient.message_id_old;

ALTER TABLE public.recipient ADD CONSTRAINT recipient_message_id_fkey FOREIGN KEY (message_id) REFERENCES public.message(id);
ALTER TABLE public.recipient DROP COLUMN message_id_old;
ALTER TABLE public.message DROP COLUMN id_old;

