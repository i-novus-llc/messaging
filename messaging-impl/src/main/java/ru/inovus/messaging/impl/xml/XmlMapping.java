package ru.inovus.messaging.impl.xml;

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
//          Не выбросится
            throw new RuntimeException(e);
        }
    }

    @XmlElement(name = "user")
    public XmlMappingEntity userMapping;
    @XmlElement(name = "role")
    public XmlMappingEntity roleMapping;
}
