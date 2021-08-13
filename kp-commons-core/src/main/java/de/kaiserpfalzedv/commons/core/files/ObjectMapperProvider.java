package de.kaiserpfalzedv.commons.core.files;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import java.text.SimpleDateFormat;

/**
 * @author rlichti
 * @version 2.0.2 2021-08-13
 * @since 2.0.2 2021-08-13
 */
@Dependent
@Slf4j
public class ObjectMapperProvider {
    @Produces
    public ObjectMapper provideMapper() {
        ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(new JavaTimeModule());
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ"));

        return mapper;
    }
}
