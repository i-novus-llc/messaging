ALTER TABLE user_setting ADD CONSTRAINT fk_user_setting_id FOREIGN KEY (id) REFERENCES message_setting(id);