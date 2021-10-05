package ru.inovus.messaging.channel.email;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({EmailChannelConfiguration.class})
public @interface EnableEmailChannel {
}
