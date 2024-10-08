package ru.inovus.messaging.impl.config;

import org.apache.cxf.annotations.Provider;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Provider(value = Provider.Type.OutInterceptor)
public class JaxRsJwtHeaderInterceptor extends AbstractPhaseInterceptor<Message> {

    private static final String AUTH_HEADER_NAME = "Authorization";

    @Autowired(required = false)
    private HttpServletRequest request;

    public JaxRsJwtHeaderInterceptor() {
        super(Phase.WRITE);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void handleMessage(Message message) {
        if (request != null && request.getHeader(AUTH_HEADER_NAME) != null) {
            Map<String, List> headers = (Map<String, List>) message.get("org.apache.cxf.message.Message.PROTOCOL_HEADERS");
            if (headers != null)
                headers.put(AUTH_HEADER_NAME, Collections.singletonList(request.getHeader(AUTH_HEADER_NAME)));
        }
    }
}

