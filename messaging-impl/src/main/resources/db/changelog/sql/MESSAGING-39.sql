ALTER TABLE public.user_setting ADD column msg_setting_id integer;
UPDATE public.user_setting SET msg_setting_id = id;
ALTER TABLE public.user_setting ALTER column msg_setting_id SET NOT NULL;

ALTER TABLE public.user_setting DROP CONSTRAINT IF EXISTS fk_user_setting_id;
ALTER TABLE public.user_setting ADD CONSTRAINT user_setting_id_message_setting_id_fk FOREIGN KEY (id) REFERENCES message_setting(id);
CREATE UNIQUE INDEX user_setting_user_id_msg_setting_id_ux ON public.user_setting USING btree(user_id, msg_setting_id);

CREATE SEQUENCE user_setting_id_seq OWNED BY user_setting.id;
ALTER TABLE public.user_setting ALTER COLUMN id SET DEFAULT nextval('user_setting_id_seq');
SELECT setval('user_setting_id_seq', (SELECT MAX(id) FROM public.user_setting));