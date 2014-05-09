//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.02.07 at 02:04:14 PM EST 
//


package edu.miami.med.alext.ncbi.xml.jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for useType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="useType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Optional"/>
 *     &lt;enumeration value="Required"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "useType", namespace = "SRA.annotation")
@XmlEnum
public enum UseType {

    @XmlEnumValue("Optional")
    OPTIONAL("Optional"),
    @XmlEnumValue("Required")
    REQUIRED("Required");
    private final String value;

    UseType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static UseType fromValue(String v) {
        for (UseType c: UseType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
