package ru.inovus.messaging.n2o;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import net.n2oapp.platform.jaxrs.*;
import net.n2oapp.platform.jaxrs.autoconfigure.JaxRsClientAutoConfiguration;
import net.n2oapp.platform.jaxrs.autoconfigure.JaxRsServerAutoConfiguration;
import net.n2oapp.platform.jaxrs.autoconfigure.MissingGenericBean;
import org.apache.cxf.spring.boot.autoconfigure.CxfAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * При переходе на платформу 3+ нужно использовать
 *
 * @see net.n2oapp.platform.jaxrs.autoconfigure.JaxRsCommonAutoConfiguration
 */
@Configuration
@AutoConfigureBefore({CxfAutoConfiguration.class, JaxRsServerAutoConfiguration.class, JaxRsClientAutoConfiguration.class})
@PropertySource("classpath:/META-INF/net/n2oapp/platform/jaxrs/default.properties")
public class JaxRsCommonAutoConfiguration {

    private List<MapperConfigurer> mapperConfigurers;

    public JaxRsCommonAutoConfiguration(@Autowired(required = false) List<MapperConfigurer> mapperConfigurers) {
        this.mapperConfigurers = mapperConfigurers;
    }

    @Bean("cxfObjectMapper")
    ObjectMapper cxfObjectMapper() {
        return new RestObjectMapper(mapperConfigurers);
    }

    @Bean
    JacksonJsonProvider jsonProvider(@Qualifier("cxfObjectMapper") ObjectMapper cxfObjectMapper) {
        return new JacksonJsonProvider(cxfObjectMapper, JacksonJaxbJsonProvider.DEFAULT_ANNOTATIONS);
    }

    @Bean
    @Conditional(MissingGenericBean.class)
    public TypedParamConverter<Date> dateParameterConverter() {
        return new DateISOParameterConverter();
    }

    @Bean
    @Conditional(MissingGenericBean.class)
    public TypedParamConverter<LocalDateTime> localDateTimeParameterConverter() {
        return new LocalDateTimeISOParameterConverter();
    }

    @Bean
    public TypedParamConverter<Sort.Order> sortParameterConverter() {
        return new SortParameterConverter();
    }

    @Bean
    @Conditional(MissingGenericBean.class)
    public TypedParamConverter<ZonedDateTime> zonedDateTimeTypedParamConverter() {
        return new ZonedDateTimeParamConverter();
    }

    @Bean
    TypedParametersProvider typedParametersProvider(Set<TypedParamConverter<?>> converters) {
        return new TypedParametersProvider(converters);
    }
}
