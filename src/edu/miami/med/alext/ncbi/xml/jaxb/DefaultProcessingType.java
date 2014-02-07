//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.02.07 at 12:56:54 PM EST 
//


package edu.miami.med.alext.ncbi.xml.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 *  Identify the tools and processing steps used to produce the de novo assembly. 
 * 
 * <p>Java class for DefaultProcessingType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DefaultProcessingType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="PIPELINE" type="{}PipelineType"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DefaultProcessingType", propOrder = {

})
public class DefaultProcessingType {

    @XmlElement(name = "PIPELINE", required = true)
    protected PipelineType pipeline;

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

}
