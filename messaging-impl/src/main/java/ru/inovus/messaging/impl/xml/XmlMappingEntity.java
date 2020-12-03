package ru.inovus.messaging.impl.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class XmlMappingEntity {
    @XmlElement(name = "response")
    public XmlMappingFields response;

    @XmlElement(name = "criteria")
    public XmlMappingFields criteria;

    @XmlAttribute(name = "content-mapping")
    public String contentMapping;

    @XmlAttribute(name = "count-mapping")
    public String countMapping;
}