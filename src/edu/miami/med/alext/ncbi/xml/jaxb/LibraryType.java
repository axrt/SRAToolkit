//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.02.07 at 01:44:35 PM EST 
//


package edu.miami.med.alext.ncbi.xml.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LibraryType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LibraryType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DESIGN_DESCRIPTION" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SAMPLE_DESCRIPTOR" type="{}SampleDescriptorType"/>
 *         &lt;element name="LIBRARY_DESCRIPTOR" type="{}LibraryDescriptorType"/>
 *         &lt;element name="SPOT_DESCRIPTOR" type="{}SpotDescriptorType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LibraryType", propOrder = {
    "designdescription",
    "sampledescriptor",
    "librarydescriptor",
    "spotdescriptor"
})
public class LibraryType {

    @XmlElement(name = "DESIGN_DESCRIPTION", required = true)
    protected String designdescription;
    @XmlElement(name = "SAMPLE_DESCRIPTOR", required = true)
    protected SampleDescriptorType sampledescriptor;
    @XmlElement(name = "LIBRARY_DESCRIPTOR", required = true)
    protected LibraryDescriptorType librarydescriptor;
    @XmlElement(name = "SPOT_DESCRIPTOR")
    protected SpotDescriptorType spotdescriptor;

    /**
     * Gets the value of the designdescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDESIGNDESCRIPTION() {
        return designdescription;
    }

    /**
     * Sets the value of the designdescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDESIGNDESCRIPTION(String value) {
        this.designdescription = value;
    }

    /**
     * Gets the value of the sampledescriptor property.
     * 
     * @return
     *     possible object is
     *     {@link SampleDescriptorType }
     *     
     */
    public SampleDescriptorType getSAMPLEDESCRIPTOR() {
        return sampledescriptor;
    }

    /**
     * Sets the value of the sampledescriptor property.
     * 
     * @param value
     *     allowed object is
     *     {@link SampleDescriptorType }
     *     
     */
    public void setSAMPLEDESCRIPTOR(SampleDescriptorType value) {
        this.sampledescriptor = value;
    }

    /**
     * Gets the value of the librarydescriptor property.
     * 
     * @return
     *     possible object is
     *     {@link LibraryDescriptorType }
     *     
     */
    public LibraryDescriptorType getLIBRARYDESCRIPTOR() {
        return librarydescriptor;
    }

    /**
     * Sets the value of the librarydescriptor property.
     * 
     * @param value
     *     allowed object is
     *     {@link LibraryDescriptorType }
     *     
     */
    public void setLIBRARYDESCRIPTOR(LibraryDescriptorType value) {
        this.librarydescriptor = value;
    }

    /**
     * Gets the value of the spotdescriptor property.
     * 
     * @return
     *     possible object is
     *     {@link SpotDescriptorType }
     *     
     */
    public SpotDescriptorType getSPOTDESCRIPTOR() {
        return spotdescriptor;
    }

    /**
     * Sets the value of the spotdescriptor property.
     * 
     * @param value
     *     allowed object is
     *     {@link SpotDescriptorType }
     *     
     */
    public void setSPOTDESCRIPTOR(SpotDescriptorType value) {
        this.spotdescriptor = value;
    }

}
