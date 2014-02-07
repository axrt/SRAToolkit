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
 * <p>Java class for ProcessingType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProcessingType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PIPELINE" type="{}PipelineType" minOccurs="0"/>
 *         &lt;element name="DIRECTIVES" type="{}SequencingDirectivesType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProcessingType", propOrder = {
    "pipeline",
    "directives"
})
public class ProcessingType {

    @XmlElement(name = "PIPELINE")
    protected PipelineType pipeline;
    @XmlElement(name = "DIRECTIVES")
    protected SequencingDirectivesType directives;

    /**
     * Gets the value of the pipeline property.
     * 
     * @return
     *     possible object is
     *     {@link PipelineType }
     *     
     */
    public PipelineType getPIPELINE() {
        return pipeline;
    }

    /**
     * Sets the value of the pipeline property.
     * 
     * @param value
     *     allowed object is
     *     {@link PipelineType }
     *     
     */
    public void setPIPELINE(PipelineType value) {
        this.pipeline = value;
    }

    /**
     * Gets the value of the directives property.
     * 
     * @return
     *     possible object is
     *     {@link SequencingDirectivesType }
     *     
     */
    public SequencingDirectivesType getDIRECTIVES() {
        return directives;
    }

    /**
     * Sets the value of the directives property.
     * 
     * @param value
     *     allowed object is
     *     {@link SequencingDirectivesType }
     *     
     */
    public void setDIRECTIVES(SequencingDirectivesType value) {
        this.directives = value;
    }

}
