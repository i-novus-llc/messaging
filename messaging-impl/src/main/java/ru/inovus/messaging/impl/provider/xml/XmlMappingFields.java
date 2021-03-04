package ru.inovus.messaging.impl.provider.xml;

import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAnyElement;
import java.util.List;

public class XmlMappingFields {
    @XmlAnyElement
    public List<Element> fields;
}
