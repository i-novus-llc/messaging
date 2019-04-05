ALTER TABLE public.message_setting ADD COLUMN send_notice boolean;
ALTER TABLE public.message_setting ADD COLUMN send_email boolean;

UPDATE public.message_setting
SET send_notice = true WHERE info_type='NOTICE';
UPDATE public.message_setting
SET send_email = true WHERE info_type='EMAIL';
UPDATE public.message_setting
SET send_notice = true, send_email = true WHERE info_type='ALL';

ALTER TABLE public.message_setting DROP COLUMN info_type;