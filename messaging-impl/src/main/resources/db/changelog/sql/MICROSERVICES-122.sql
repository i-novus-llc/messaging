ALTER TABLE public.message
  DROP CONSTRAINT IF EXISTS message_severity_check RESTRICT;

UPDATE public.message SET severity = '10' WHERE severity = 'INFO';
UPDATE public.message SET severity = '20' WHERE severity = 'WARNING';
UPDATE public.message SET severity = '30' WHERE severity = 'ERROR';
UPDATE public.message SET severity = '40' WHERE severity = 'SEVERE';

ALTER TABLE public.message
  ADD CONSTRAINT message_severity_check CHECK ((severity)::text = ANY ((ARRAY['40'::character varying, '30'::character varying, '20'::character varying, '10'::character varying])::text[]));