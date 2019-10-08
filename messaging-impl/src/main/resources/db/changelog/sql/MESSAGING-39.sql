ALTER TABLE public.user_setting ADD column msg_settings_id integer;
UPDATE public.user_setting SET msg_settings_id = id;
ALTER TABLE public.user_setting ALTER column msg_settings_id SET NOT NULL;

ALTER TABLE public.user_setting DROP CONSTRAINT IF EXISTS fk_user_setting_id;
ALTER TABLE public.user_setting ADD CONSTRAINT user_setting_id_message_setting_id_fk FOREIGN KEY (id) REFERENCES message_setting(id);
