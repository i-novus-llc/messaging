alter table messaging.recipient_group_template
drop constraint recipient_group_template_message_template_id_fkey;

alter table messaging.recipient_group_template
drop constraint recipient_group_template_recipient_group_id_fkey;

alter table messaging.recipient_group_user
drop constraint recipient_group_user_recipient_group_id_fkey;

alter table messaging.recipient_group_template
    add constraint recipient_group_template_message_template_id_fkey
        foreign key (message_template_id)
            references messaging.message_template (id) on delete cascade on update cascade;

alter table messaging.recipient_group_template
    add constraint recipient_group_template_recipient_group_id_fkey
        foreign key (recipient_group_id)
            references messaging.recipient_group (id) on delete cascade on update cascade;

alter table messaging.recipient_group_user
    add constraint recipient_group_user_recipient_group_id_fkey
        foreign key (recipient_group_id)
            references messaging.recipient_group (id) on delete cascade on update cascade;
