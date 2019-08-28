alter table message_setting add column code varchar not null;

comment on column message_setting.code is 'Код шаблона сообщения';

create unique index code_ux on message_setting (code);

insert into messaging.public.message_setting (id, caption,
                                              text,
                                              severity, alert_type, component_id, name,
                                              formation_type, send_notice, send_email, code) VALUES
(nextval(message_setting_id_seq), 'Зарегистрирован договор № %CONTRACT_NUMBER%',
'В личном кабинете банка Единой информационной системы жилищного строительства зарегистрирован новый договор поручительства № %CONTRACT_NUMBER% от %CONTRACT_DATE%", застройщик %DEVELOPER_FULL_NAME%.<br><br><a href=''%URL%''>Перейти к карточке договора</a>',
'10', 'HIDDEN', 1, 'Уведомление УЛ банка о создании нового договора',
'AUTO', true, true,'LKB-PAYMENT-NTF-1');
insert into messaging.public.message_setting (id, caption,
                                              text,
                                              severity, alert_type, component_id, name,
                                              formation_type, send_notice, send_email, code) VALUES
(nextval(message_setting_id_seq), 'Зарегистрирован договор № %CONTRACT_NUMBER%',
'В личном кабинете банка Единой информационной системы жилищного строительства зарегистрирован новый договор поручительства № %CONTRACT_NUMBER% от %CONTRACT_DATE%, застройщик %DEVELOPER_FULL_NAME%.<br><br><a href=''%URL%''>Перейти к карточке договора</a>',
'10', 'HIDDEN', 1, 'Уведомление УЛ ДОМ.РФ о создании нового договора',
'AUTO', true, true,'LKB-PAYMENT-NTF-2');
insert into messaging.public.message_setting (id, caption,
                                              text,
                                              severity, alert_type, component_id, name,
                                              formation_type, send_notice, send_email, code) VALUES
(nextval(message_setting_id_seq), 'Предоставление сведений о выборке кредитных средств по договору № %CONTRACT_NUMBER%',
'По договору № %CONTRACT_NUMBER% от %CONTRACT_DATE%, застройщик %DEVELOPER_FULL_NAME$ доступна загрузка файла выборки кредитных средств за %REPORTING_PERIOD_FULL_NAME%.<br><br><a href=''%URL%''>Перейти к карточке договора</a>',
'10', 'HIDDEN', 1, 'Уведомление УЛ банка о необходимости предоставления выборки по договору за новый отчетный период',
'AUTO', true, true,'LKB-PAYMENT-NTF-3');
insert into messaging.public.message_setting (id, caption,
                                              text,
                                              severity, alert_type, component_id, name,
                                              formation_type, send_notice, send_email, code) VALUES
(nextval(message_setting_id_seq), 'Предоставлен файл выборки кредитных средств по договору № %CONTRACT_NUMBER%',
'Банком загружен файл с выборкой кредитных средств за %REPORTING_PERIOD_FULL_NAME% по договору № %CONTRACT_NUMBER%, застройщик %DEVELOPER_FULL_NAME%.<br><br>Необходимо провести расчет и начисление комиссии за поручительство.<br><br><a href=''%URL%''>Перейти к карточке договора</a>',
'10', 'HIDDEN', 1, 'Уведомление УЛ ДОМ.РФ о загрузке банком выборки по договору',
'AUTO', true, true, 'LKB-PAYMENT-NTF-4');
insert into messaging.public.message_setting (id, caption,
                                              text,
                                              severity, alert_type, component_id, name,
                                              formation_type, send_notice, send_email, code) VALUES
(nextval(message_setting_id_seq), 'Начисление комиссии по договору № %CONTRACT_NUMBER%',
'По договору № %CONTRACT_NUMBER%, застройщик %DEVELOPER_FULL_NAME% выполнено %OPERATION_NAME%.<br><br><br><br>Необходимо оплатить комиссию в срок, установленный условиями договора.<br><br>С суммой начисленной комиссии Вы можете ознакомиться на карточке договора.<br><br><a href=''%URL%''>Перейти к карточке договора</a>',
'10', 'HIDDEN', 1, 'Уведомление УЛ банка о выполнении операций по начислению комиссии по договору',
'AUTO', true, true, 'LKB-PAYMENT-NTF-5');
insert into messaging.public.message_setting (id, caption,
                                              text,
                                              severity, alert_type, component_id, name,
                                              formation_type, send_notice, send_email, code) VALUES
(nextval(message_setting_id_seq), 'Корректировка комиссии по договору № %CONTRACT_NUMBER%',
'По договору № %CONTRACT_NUMBER%, застройщик %DEVELOPER_FULL_NAME% выполнена %OPERATION_NAME%.<br><br>С актуальной суммой начисленной комиссии Вы можете ознакомиться на карточке договора.<br><br><a href=''%URL%''>Перейти к карточке договора</a>',
'10', 'HIDDEN', 1, 'Уведомление УЛ банка о выполнении операций по корректировке комиссии по договору',
'AUTO', true, true, 'LKB-PAYMENT-NTF-6');
insert into messaging.public.message_setting (id, caption,
                                              text,
                                              severity, alert_type, component_id, name,
                                              formation_type, send_notice, send_email, code) VALUES
(nextval(message_setting_id_seq), 'Оплата комиссии по договору № %CONTRACT_NUMBER%',
'По договору № %CONTRACT_NUMBER%, застройщик %DEVELOPER_FULL_NAME% выполнена %OPERATION_NAME%.<br><br><a href=''%URL%''>Перейти к карточке договора</a>',
'10', 'HIDDEN', 1, 'Уведомление УЛ банка о выполнении операций по погашению комиссии по договору',
'AUTO', true, true, 'LKB-PAYMENT-NTF-7');
insert into messaging.public.message_setting (id, caption,
                                              text,
                                              severity, alert_type, component_id, name,
                                              formation_type, send_notice, send_email, code) VALUES
(nextval(message_setting_id_seq), 'Файл выборки по договору № %CONTRACT_NUMBER$ отклонен',
'Загруженный файл с выборкой кредитных средств за %REPORTING_PERIOD_FULL_NAME% по договору № %CONTRACT_NUMBER$, застройщик %DEVELOPER_FULL_NAME% отклонен ДОМ.РФ.<br><br><br><br>Необходимо внести необходимые исправления в файл выборки кредитных средств и загрузить обновленный файл в карточке договора.<br><br><a href=''%URL%''>Перейти к карточке договора</a>',
'10', 'HIDDEN', 1, 'Уведомление УЛ банка об отклонении предоставленной выборки кредитных средств',
'AUTO', true, true, 'LKB-PAYMENT-NTF-9');
insert into messaging.public.message_setting (id, caption,
                                              text,
                                              severity, alert_type, component_id, name,
                                              formation_type, send_notice, send_email, code) VALUES
(nextval(message_setting_id_seq), 'Изменение статуса договора № %CONTRACT_NUMBER%',
'Статус по договору № %CONTRACT_NUMBER%, застройщик %DEVELOPER_FULL_NAME% изменен на %CONTRACT_STATUS_NAME%.<br><br><a href=''%URL%''>Перейти к карточке договора</a>',
'10', 'HIDDEN', 1, 'Уведомление УЛ банка об изменении статуса договора поручительства',
'AUTO', true, true, 'LKB-PAYMENT-NTF-10');
insert into messaging.public.message_setting (id, caption,
                                              text,
                                              severity, alert_type, component_id, name,
                                              formation_type, send_notice, send_email, code) VALUES
(nextval(message_setting_id_seq), 'Ожидается выборка по договору № %CONTRACT_NUMBER%',
'Необходимо загрузить файл с выборкой кредитных средств за %REPORTING_PERIOD_FULL_NAME% по договору № %CONTRACT_NUMBER%, застройщик %DEVELOPER_FULL_NAME%.<br><br>Загрузка файла выборки должна быть произведена до %DATE% (включительно).<br><br><a href=''%URL%''>Перейти к карточке договора</a><br><br>В случае непредоставления файла в указанный срок будет произведено начисление пени по договору.',
'10', 'HIDDEN', 1, 'Уведомление УЛ банка о необходимости предоставления выборки по договору (на 12ий день)',
'AUTO', true, true, 'LKB-PAYMENT-NTF-11');
insert into messaging.public.message_setting (id, caption,
                                              text,
                                              severity, alert_type, component_id, name,
                                              formation_type, send_notice, send_email, code) VALUES
(nextval(message_setting_id_seq), 'Отсутствие выборки по договору № %CONTRACT_NUMBER%',
'Банком %BANK_FULL_NAME% в установленный срок %DATE% не был загружен файл с выборкой кредитных средств за %REPORTING_PERIOD_FULL_NAME% по договору № %CONTRACT_NUMBER%, застройщик %DEVELOPER_FULL_NAME%.<br><br>Необходимо направить в банк письменное уведомление о начале начисления пени по договору.<br><br><a href=''%URL%''>Перейти к карточке договора</a>',
'10', 'HIDDEN', 1, 'Уведомление УЛ ДОМ.РФ об отсутствии выборки по договору и необходимости сформировать письменное обращение в банк',
'AUTO', true, true, 'LKB-PAYMENT-NTF-12');
insert into messaging.public.message_setting (id, caption,
                                              text,
                                              severity, alert_type, component_id, name,
                                              formation_type, send_notice, send_email, code) VALUES
(nextval(message_setting_id_seq), 'Отсутствие выборки по договору № %CONTRACT_NUMBER%',
'По договору № %CONTRACT_NUMBER%, застройщик %DEVELOPER_FULL_NAME% в установленный срок не были предоставлены сведения о выборке кредитных средств за %REPORTING_PERIOD_FULL_NAME%.<br><br>По договору поручительства будет производится начисление пени.<br><br><a href=''%URL%''>Перейти к карточке договора</a>',
'10', 'HIDDEN', 1, 'Уведомление УЛ банка об отсутствии выборки по договору',
'AUTO', true, true, 'LKB-PAYMENT-NTF-13');
insert into messaging.public.message_setting (id, caption,
                                              text,
                                              severity, alert_type, component_id, name,
                                              formation_type, send_notice, send_email, code) VALUES
(nextval(message_setting_id_seq), 'Использование базы для расчета по договору № %CONTRACT_NUMBER%',
'В случае непредоставления сведений о выборке кредитных средств за %REPORTING_PERIOD_FULL_NAME% начисление комиссий по договору № %CONTRACT_NUMBER%, застройщик %DEVELOPER_FULL_NAME% будет производится по данным, предоставленным за предыдущие периоды<br><br><a href=''%URL%''>Перейти к карточке договора</a>',
'10', 'HIDDEN', 1, 'Уведомлении УЛ банка о начислении комиссии на основе сведений о выборках, предоставленных за предыдущие периоды',
'AUTO', true, true, 'LKB-PAYMENT-NTF-14');
insert into messaging.public.message_setting (id, caption,
                                              text,
                                              severity, alert_type, component_id, name,
                                              formation_type, send_notice, send_email, code) VALUES
(nextval(message_setting_id_seq), 'Напоминание о необходимости предоставления сведений по договору № %CONTRACT_NUMBER%',
'По договору № %CONTRACT_NUMBER%, застройщик %DEVELOPER_FULL_NAME% не предоставлены сведения о выборке кредитных средств.<br><br>Необходимо загрузить файл выборки кредитных средств на карточке договора.<br><br><a href=''%URL%''>Перейти к карточке договора</a>',
'10', 'HIDDEN', 1, 'Уведомление УЛ банка о необходимости предоставления сведения о выборках кредитных средств',
'AUTO', true, true, 'LKB-PAYMENT-NTF-15');
insert into messaging.public.message_setting (id, caption,
                                              text,
                                              severity, alert_type, component_id, name,
                                              formation_type, send_notice, send_email, code) VALUES
(nextval(message_setting_id_seq), 'Предупреждение о расторжении договора № %CONTRACT_NUMBER%',
'По договору № %CONTRACT_NUMBER%, застройщик %DEVELOPER_FULL_NAME% имеется неоплаченная задолженность за %REPORTING_PERIOD_FULL_NAME%.<br><br>Согласно условиям договора, в случае неоплаты комиссии договор будет расторгнут %DATE%.<br><br><a href=''%URL%''>Перейти к карточке договора</a>',
'10', 'HIDDEN', 1, 'Уведомление УЛ банка о расторжении договора в случае неоплаты премии согласно условиям договора',
'AUTO', true, true, 'LKB-PAYMENT-NTF-16');
insert into messaging.public.message_setting (id, caption,
                                              text,
                                              severity, alert_type, component_id, name,
                                              formation_type, send_notice, send_email, code) VALUES
(nextval(message_setting_id_seq), 'Предупреждение о расторжении договора № %CONTRACT_NUMBER%',
'По договору № %CONTRACT_NUMBER%, застройщик %DEVELOPER_FULL_NAME% имеется неоплаченная задолженность за %REPORTING_PERIOD_FULL_NAME%.<br><br>Согласно условиям договора, в случае неоплаты комиссии договор будет расторгнут %DATE%.<br><br>Необходимо направить письменное уведомление в банк и начислить пени за неоплату комиссии за поручительство.<br><br><a href=''%URL%''>Перейти к карточке договора</a>',
'10', 'HIDDEN', 1, 'Уведомление УЛ ДОМ.РФ о приближении срока расторжении договора по причине неоплаты комиссии за поручительство',
'AUTO', true, true, 'LKB-PAYMENT-NTF-17');
insert into messaging.public.message_setting (id, caption,
                                              text,
                                              severity, alert_type, component_id, name,
                                              formation_type, send_notice, send_email, code) VALUES
(nextval(message_setting_id_seq), 'Задолженность по договору № %CONTRACT_NUMBER%',
'По договору № %CONTRACT_NUMBER%, застройщик %DEVELOPER_FULL_NAME% имеется неоплаченная задолженность.<br><br>С актуальной суммой задолженности можно ознакомиться на карточке договора.<br><br><a href=''%URL%''>Перейти к карточке договора</a>',
'10', 'HIDDEN', 1, 'Уведомление УЛ банка о наличии задолженности по комиссии или пени',
'AUTO', true, true, 'LKB-PAYMENT-NTF-18');
insert into messaging.public.message_setting (id, caption,
                                              text,
                                              severity, alert_type, component_id, name,
                                              formation_type, send_notice, send_email, code) VALUES
(nextval(message_setting_id_seq), 'Отсутствие сведений о комиссии по договору № %CONTRACT_NUMBER%',
'По договору № %CONTRACT_NUMBER%, застройщик %DEVELOPER_FULL_NAME% не предоставлена выборка кредитных средств и начислена комиссия за %REPORTING_PERIOD_FULL_NAME%.<br><br>Сведения за указанный период будут удалены.<br><br><a href=''%URL%''>Перейти к карточке договора</a>',
'10', 'HIDDEN', 1, 'Уведомление УЛ ДОМ.РФ об удалении выборки за период, т.к. комиссия не была начислена',
'AUTO', true, true, 'LKB-PAYMENT-NTF-19');
insert into messaging.public.message_setting (id, caption,
                                              text,
                                              severity, alert_type, component_id, name,
                                              formation_type, send_notice, send_email, code) VALUES
(nextval(message_setting_id_seq), 'Не начислена комиссия по договору № %CONTRACT_NUMBER%',
'По договору № %CONTRACT_NUMBER%, застройщик %DEVELOPER_FULL_NAME% предоставлен файл выборки кредитных средств, но не рассчитана сумма комиссии за %REPORTING_PERIOD_FULL_NAME%.<br><br>Необходимо начислить комиссию за указанный период.<br><br><a href=''%URL%''>Перейти к карточке договора</a>',
'10', 'HIDDEN', 1, 'Уведомление УЛ ДОМ.РФ о необходимости начисления комисии',
'AUTO', true, true, 'LKB-PAYMENT-NTF-20');
insert into messaging.public.message_setting (id, caption,
                                              text,
                                              severity, alert_type, component_id, name,
                                              formation_type, send_notice, send_email, code) VALUES
(nextval(message_setting_id_seq), 'Нарушение сроков оплаты по договору № %CONTRACT_NUMBER%',
'По договору № %CONTRACT_NUMBER%, застройщик %DEVELOPER_FULL_NAME% в установленные договором сроки не оплачена комиссия за %REPORTING_PERIOD_FULL_NAME%.<br><br>Сумма комиссии вынесена на просрочку, по договору будут начислены пени за нарушение сроков оплаты.<br><br><a href=''%URL%''>Перейти к карточке договора</a>',
'10', 'HIDDEN', 1, 'Уведомление УЛ банка о выносе суммы комиссии на просрочку',
'AUTO', true, true, 'LKB-PAYMENT-NTF-21');
insert into messaging.public.message_setting (id, caption,
                                              text,
                                              severity, alert_type, component_id, name,
                                              formation_type, send_notice, send_email, code) VALUES
(nextval(message_setting_id_seq), 'Нарушение сроков оплаты по договору № %CONTRACT_NUMBER%',
'По договору № %CONTRACT_NUMBER%, застройщик %DEVELOPER_FULL_NAME% в установленные договором сроки не оплачена комиссия за %REPORTING_PERIOD_FULL_NAME%.<br><br>Сумма комиссии вынесена на просрочку, по договору необходимо начислить пени за нарушение сроков оплаты.<br><br><a href=''%URL%''>Перейти к карточке договора</a>',
'10', 'HIDDEN', 1, 'Уведомление УЛ ДОМ.РФ о выносе суммы комиссии на просрочку',
'AUTO', true, true, 'LKB-PAYMENT-NTF-22');