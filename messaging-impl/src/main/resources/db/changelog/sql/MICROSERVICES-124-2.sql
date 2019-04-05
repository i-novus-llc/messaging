ALTER TABLE public.message ADD COLUMN send_notice boolean;
ALTER TABLE public.message ADD COLUMN send_email boolean;

UPDATE public.message
SET send_notice = true WHERE info_type='NOTICE';
UPDATE public.message
SET send_email = true WHERE info_type='EMAIL';
UPDATE public.message
SET send_notice = true, send_email = true WHERE info_type='ALL';

ALTER TABLE public.message DROP COLUMN info_type;