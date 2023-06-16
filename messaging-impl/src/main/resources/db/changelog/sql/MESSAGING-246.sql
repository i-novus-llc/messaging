--update severity value
UPDATE messaging.message SET severity = 'DANGER' WHERE severity = 'ERROR';
UPDATE messaging.message SET severity = 'PRIMARY' WHERE severity = 'SEVERE';