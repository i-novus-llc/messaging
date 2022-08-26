package ru.inovus.messaging.impl.util;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MessageHelper {

    private final MessageSource messageSource;

    public MessageHelper(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String obtainMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, Locale.getDefault());
    }
}