ALTER TABLE public.message_setting
  DROP CONSTRAINT IF EXISTS message_setting_severity_check RESTRICT;

UPDATE public.message_setting SET severity = '10' WHERE severity = 'INFO';
UPDATE public.message_setting SET severity = '20' WHERE severity = 'WARNING';
UPDATE public.message_setting SET severity = '30' WHERE severity = 'ERROR';
UPDATE public.message_setting SET severity = '40' WHERE severity = 'SEVERE';

ALTER TABLE public.message_setting
  ADD CONSTRAINT message_setting_severity_check CHECK ((severity)::text = ANY ((ARRAY['40'::character varying, '30'::character varying, '20'::character varying, '10'::character varying])::text[]));