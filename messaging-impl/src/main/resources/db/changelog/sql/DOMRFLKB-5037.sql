update public.message_setting set text = replace(text, '%CONTRACT_NUMBER$', '%CONTRACT_NUMBER%') where code='LKB-PAYMENT-NTF-9';
update public.message_setting set text = replace(text, '%DEVELOPER_FULL_NAME$', '%DEVELOPER_FULL_NAME%') where code='LKB-PAYMENT-NTF-3';