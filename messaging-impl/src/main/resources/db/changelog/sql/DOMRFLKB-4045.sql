insert into public.component values (3, 'MONITORING');

insert into messaging.public.message_setting (id, caption,
                                              text,
                                              severity, alert_type, component_id, name,
                                              formation_type, send_notice, send_email, code)
select nextval('message_setting_id_seq'), 'Предоставление отчета о мониторинге по договору № %CONTRACT_NUMBER%',
       'В личном кабинете банка доступно заполнение отчета о мониторинге за %REPORTING_PERIOD_FULL_NAME% по проекту %PROJECT_NAME%, застройщик %DEVELOPER_FULL_NAME%.<br><br><a href=''%URL%''>Перейти к карточке отчета</a>',
       '10', 'HIDDEN', 3, 'Уведомление о создании нового отчета о мониторинге',
       'AUTO', true, true, 'LKB-MONITORING-NTF-1';
insert into messaging.public.message_setting (id, caption,
                                              text,
                                              severity, alert_type, component_id, name,
                                              formation_type, send_notice, send_email, code)
select nextval('message_setting_id_seq'), 'Назначение отчета о мониторинге по договору № %CONTRACT_NUMBER%',
       'Вы указаны исполнителем в отчете о мониторинге за %REPORTING_PERIOD_FULL_NAME% по проекту %PROJECT_NAME%, застройщик %DEVELOPER_FULL_NAME%.<br><br><a href=''%URL%''>Перейти к карточке отчета</a>',
       '10', 'HIDDEN', 3, 'Уведомление сотрудника банка о назначении отчета о мониторинге',
       'AUTO', true, true, 'LKB-MONITORING-NTF-2';
insert into messaging.public.message_setting (id, caption,
                                              text,
                                              severity, alert_type, component_id, name,
                                              formation_type, send_notice, send_email, code)
select nextval('message_setting_id_seq'), 'Напоминание о предоставлении отчета о мониторинге по договору № %CONTRACT_NUMBER%',
       'В личном кабинете банка не направлен отчет о мониторинге за %REPORTING_PERIOD_FULL_NAME% по проекту %PROJECT_NAME%, застройщик %DEVELOPER_FULL_NAME%.<br><br><a href=''%URL%''>Перейти к карточке отчета</a>',
       '10', 'HIDDEN', 3, 'Напоминание о необходимости заполнения отчета',
       'AUTO', true, true, 'LKB-MONITORING-NTF-3';
insert into messaging.public.message_setting (id, caption,
                                              text,
                                              severity, alert_type, component_id, name,
                                              formation_type, send_notice, send_email, code)
select nextval('message_setting_id_seq'), 'Получен отчет о мониторинге по договору № %CONTRACT_NUMBER%',
       'Получен отчет о мониторинге за %REPORTING_PERIOD_FULL_NAME% по проекту %PROJECT_NAME%, застройщик %DEVELOPER_FULL_NAME%. Необходимо назначить сотрудника кредитного подразделения для работы с отчетом.<br><br><a href=''%URL%''>Перейти к карточке отчета</a>',
       '10', 'HIDDEN', 3, 'Уведомление о получении нового отчета о мониторинге',
       'AUTO', true, true, 'LKB-MONITORING-NTF-4';
insert into messaging.public.message_setting (id, caption,
                                              text,
                                              severity, alert_type, component_id, name,
                                              formation_type, send_notice, send_email, code)
select nextval('message_setting_id_seq'), 'Назначение отчета о мониторинге по договору № %CONTRACT_NUMBER%',
       'Вы указаны исполнителем в отчете о мониторинге за %REPORTING_PERIOD_FULL_NAME% по проекту %PROJECT_NAME%, застройщик %DEVELOPER_FULL_NAME%.<br><br><a href=''%URL%''>Перейти к карточке отчета</a>',
       '10', 'HIDDEN', 3, 'Уведомление сотрудника КП о назначении отчета о мониторинге',
       'AUTO', true, true, 'LKB-MONITORING-NTF-5';
insert into messaging.public.message_setting (id, caption,
                                              text,
                                              severity, alert_type, component_id, name,
                                              formation_type, send_notice, send_email, code)
select nextval('message_setting_id_seq'), 'Назначение отчета о мониторинге по договору № %CONTRACT_NUMBER%',
       'Вы указаны исполнителем в отчете о мониторинге за %REPORTING_PERIOD_FULL_NAME% по проекту %PROJECT_NAME%, застройщик %DEVELOPER_FULL_NAME%.<br><br><a href=''%URL%''>Перейти к карточке отчета</a>',
       '10', 'HIDDEN', 3, 'Уведомление сотрудника ЭП о назначении отчета о мониторинге',
       'AUTO', true, true, 'LKB-MONITORING-NTF-6';
insert into messaging.public.message_setting (id, caption,
                                              text,
                                              severity, alert_type, component_id, name,
                                              formation_type, send_notice, send_email, code)
select nextval('message_setting_id_seq'), 'Назначение отчета о мониторинге по договору № %CONTRACT_NUMBER%',
       'Вы указаны исполнителем в отчете о мониторинге за %REPORTING_PERIOD_FULL_NAME% по проекту %PROJECT_NAME%, застройщик %DEVELOPER_FULL_NAME%.<br><br><a href=''%URL%''>Перейти к карточке отчета</a>',
       '10', 'HIDDEN', 3, 'Уведомление сотрудника АР о назначении отчета о мониторинге',
       'AUTO', true, true, 'LKB-MONITORING-NTF-7';
insert into messaging.public.message_setting (id, caption,
                                              text,
                                              severity, alert_type, component_id, name,
                                              formation_type, send_notice, send_email, code)
select nextval('message_setting_id_seq'), 'Назначение исполнителя в отчете о мониторинге по договору № %CONTRACT_NUMBER%',
       'Создан запрос на обработку отчета в %DEPARTMENT_FULL_NAME% для отчета о мониторинге за %REPORTING_PERIOD_FULL_NAME% по проекту %PROJECT_NAME%, застройщик %DEVELOPER_FULL_NAME%. Необходимо назначить сотрудника экспертного подразделения для работы с отчетом.<br><br><a href=''%URL%''>Перейти к карточке отчета</a>',
       '10', 'HIDDEN', 3, 'Уведомление Администратора ДОМ.РФ о необходимости назначения сотрудника ЭП',
       'AUTO', true, true, 'LKB-MONITORING-NTF-8';
insert into messaging.public.message_setting (id, caption,
                                              text,
                                              severity, alert_type, component_id, name,
                                              formation_type, send_notice, send_email, code)
select nextval('message_setting_id_seq'), 'Получено заключение ЭП в отчете о мониторинге по договору № %CONTRACT_NUMBER%',
       '%DEPARTMENT_FULL_NAME% заполнило заключение в отчете о мониторинге за %REPORTING_PERIOD_FULL_NAME% по проекту %PROJECT_NAME%, застройщик %DEVELOPER_FULL_NAME%.<br><br><a href=''%URL%''>Перейти к карточке отчета</a>',
       '10', 'HIDDEN', 3, 'Уведомление о завершении проверки отчета сотрудником ЭП',
       'AUTO', true, true, 'LKB-MONITORING-NTF-9';
insert into messaging.public.message_setting (id, caption,
                                              text,
                                              severity, alert_type, component_id, name,
                                              formation_type, send_notice, send_email, code)
select nextval('message_setting_id_seq'), 'Необходима дополнительная информация в отчете о мониторинге по договору № %CONTRACT_NUMBER%',
       'Получен новый запрос на предоставление дополнительной информации вотчете о мониторинге за %REPORTING_PERIOD_FULL_NAME% по проекту %PROJECT_NAME%, застройщик %DEVELOPER_FULL_NAME%.<br><br><a href=''%URL%''>Перейти к запросам доп.информации по отчету</a>',
       '10', 'HIDDEN', 3, 'Уведомление банка о новом запросе дополнительной информации',
       'AUTO', true, true, 'LKB-MONITORING-NTF-10';
insert into messaging.public.message_setting (id, caption,
                                              text,
                                              severity, alert_type, component_id, name,
                                              formation_type, send_notice, send_email, code)
select nextval('message_setting_id_seq'), 'Новое сообщение в запросе доп.информации для отчета о мониторинге по договору № %CONTRACT_NUMBER%',
       'Получено новое сообщение в запросе дополнительной информации для отчета о мониторинге за %REPORTING_PERIOD_FULL_NAME% по проекту %PROJECT_NAME%, застройщик %DEVELOPER_FULL_NAME%.<br><br><a href=''%URL%''>Перейти к запросам доп.информации по отчету</a>',
       '10', 'HIDDEN', 3, 'Уведомление банка о новом сообщении в запросе дополнительной информации',
       'AUTO', true, true, 'LKB-MONITORING-NTF-11';
insert into messaging.public.message_setting (id, caption,
                                              text,
                                              severity, alert_type, component_id, name,
                                              formation_type, send_notice, send_email, code)
select nextval('message_setting_id_seq'), 'Новое сообщение в запросе доп.информации для отчета о мониторинге по договору № %CONTRACT_NUMBER%',
       'Получено новое сообщение в запросе дополнительной информации для отчета о мониторинге за %REPORTING_PERIOD_FULL_NAME% по проекту %PROJECT_NAME%, застройщик %DEVELOPER_FULL_NAME%.<br><br><a href=''%URL%''>Перейти к запросам доп.информации по отчету</a>',
       '10', 'HIDDEN', 3, 'Уведомление сотрудников ДОМ.РФ об ответе банка в запросах доп.информации',
       'AUTO', true, true, 'LKB-MONITORING-NTF-12';
insert into messaging.public.message_setting (id, caption,
                                              text,
                                              severity, alert_type, component_id, name,
                                              formation_type, send_notice, send_email, code)
select nextval('message_setting_id_seq'), 'Получено заключение АР в отчете о мониторинге по договору № %CONTRACT_NUMBER%',
       'Произведен андеррайтинг отчета о мониторинге за %REPORTING_PERIOD_FULL_NAME% по проекту %PROJECT_NAME%, застройщик %DEVELOPER_FULL_NAME%.<br><br><a href=''%URL%''>Перейти к карточке отчета</a>',
       '10', 'HIDDEN', 3, 'Уведомление о завершении проверки отчета сотрудником АР',
       'AUTO', true, true, 'LKB-MONITORING-NTF-13';
insert into messaging.public.message_setting (id, caption,
                                              text,
                                              severity, alert_type, component_id, name,
                                              formation_type, send_notice, send_email, code)
select nextval('message_setting_id_seq'), 'Назначение исполнителя в отчете о мониторинге по договору № %CONTRACT_NUMBER%',
       'На верификацию в подразделение андеррайтинга направлен отчет о мониторинге за %REPORTING_PERIOD_FULL_NAME% по проекту %PROJECT_NAME%, застройщик %DEVELOPER_FULL_NAME%. Необходимо назначить сотрудника андеррайтинга для работы с отчетом.<br><br><a href=''%URL%''>Перейти к карточке отчета</a>',
       '10', 'HIDDEN', 3, 'Уведомление Администратора ДОМ.РФ о необходимости назначения сотрудника АР',
       'AUTO', true, true, 'LKB-MONITORING-NTF-14';
insert into messaging.public.message_setting (id, caption,
                                              text,
                                              severity, alert_type, component_id, name,
                                              formation_type, send_notice, send_email, code)
select nextval('message_setting_id_seq'), 'Назначение сотрудника ЭП для отчета о мониторинге по договору № %CONTRACT_NUMBER%',
       'Назначен сотрудник ЭП (%DEPARTMENT_FULL_NAME%) в отчете о мониторинге за %REPORTING_PERIOD_FULL_NAME% по проекту %PROJECT_NAME%, застройщик %DEVELOPER_FULL_NAME%.<br><br><a href=''%URL%''>Перейти к карточке отчета</a>',
       '10', 'HIDDEN', 3, 'Уведомление сотрудника КП о назначении ЭП для работы с отчетом',
       'AUTO', true, true, 'LKB-MONITORING-NTF-15';
