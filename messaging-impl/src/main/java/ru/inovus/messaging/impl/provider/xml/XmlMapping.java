package ru.inovus.messaging.impl.provider.xml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "mapping")
public class XmlMapping {

    public static final JAXBContext JAXB_CONTEXT;

    static {
        try {
            JAXB_CONTEXT = JAXBContext.newInstance(XmlMapping.class);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    @XmlElement(name = "recipient")
    public XmlMappingEntity recipientMapping;
}
