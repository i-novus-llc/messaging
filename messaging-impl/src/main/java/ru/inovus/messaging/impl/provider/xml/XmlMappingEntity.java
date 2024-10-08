package ru.inovus.messaging.impl.provider.xml;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

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