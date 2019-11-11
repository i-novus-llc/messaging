ALTER TABLE public.user_setting DROP CONSTRAINT IF EXISTS user_setting_id_message_setting_id_fk;

ALTER TABLE public.user_setting ADD CONSTRAINT user_setting_msg_settings_id_message_setting_id_fk FOREIGN KEY (msg_setting_id) REFERENCES message_setting(id);
