ALTER TABLE public.user_setting ADD COLUMN send_notice boolean;
ALTER TABLE public.user_setting ADD COLUMN send_email boolean;

UPDATE public.user_setting
SET send_notice = true WHERE info_type='NOTICE';
UPDATE public.user_setting
SET send_email = true WHERE info_type='EMAIL';
UPDATE public.user_setting
SET send_notice = true, send_email = true WHERE info_type='ALL';

ALTER TABLE public.user_setting DROP COLUMN info_type;