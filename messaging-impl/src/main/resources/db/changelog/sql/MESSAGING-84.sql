ALTER TABLE public.message ADD COLUMN send_email_date TIMESTAMP;
ALTER TABLE public.message ADD COLUMN send_email_error VARCHAR;

COMMENT ON COLUMN message.send_email_date IS 'Дата и время отправки email';
COMMENT ON COLUMN message.send_email_error IS 'Ошибка, возникшая при последней попытке отправки email';
