package ru.inovus.messaging.channel.web.configuration;


import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({WebChannelConfiguration.class})
public @interface EnableWebChannel { }
