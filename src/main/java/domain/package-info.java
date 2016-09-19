@org.hibernate.annotations.GenericGenerator(
    name = "ID_GENERATOR",
    strategy = "enhanced-sequence"
)

@XmlJavaTypeAdapters({
    @XmlJavaTypeAdapter(type=DateTime.class, 
        value=DateTimeAdapter.class)})

package domain;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;

import jaxb.DateTimeAdapter;

import org.joda.time.DateTime;

