//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.02.07 at 02:04:14 PM EST 
//


package xml.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ExperimentPackageType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExperimentPackageType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="EXPERIMENT" type="{}ExperimentType"/>
 *         &lt;element name="SUBMISSION" type="{}SubmissionType"/>
 *         &lt;element name="STUDY" type="{}StudyType"/>
 *         &lt;element name="SAMPLE" type="{}SampleType"/>
 *         &lt;element name="RUN_SET" type="{}RunSetType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExperimentPackageType", propOrder = {
    "experiment",
    "submission",
    "study",
    "sample",
    "runset"
})
public class ExperimentPackageType {

    @XmlElement(name = "EXPERIMENT", required = true)
    protected ExperimentType experiment;
    @XmlElement(name = "SUBMISSION", required = true)
    protected SubmissionType submission;
    @XmlElement(name = "STUDY", required = true)
    protected StudyType study;
    @XmlElement(name = "SAMPLE", required = true)
    protected SampleType sample;
    @XmlElement(name = "RUN_SET", required = true)
    protected RunSetType runset;

    /**
     * Gets the value of the experiment property.
     * 
     * @return
     *     possible object is
     *     {@link ExperimentType }
     *     
     */
    public ExperimentType getEXPERIMENT() {
        return experiment;
    }

    /**
     * Sets the value of the experiment property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExperimentType }
     *     
     */
    public void setEXPERIMENT(ExperimentType value) {
        this.experiment = value;
    }

    /**
     * Gets the value of the submission property.
     * 
     * @return
     *     possible object is
     *     {@link SubmissionType }
     *     
     */
    public SubmissionType getSUBMISSION() {
        return submission;
    }

    /**
     * Sets the value of the submission property.
     * 
     * @param value
     *     allowed object is
     *     {@link SubmissionType }
     *     
     */
    public void setSUBMISSION(SubmissionType value) {
        this.submission = value;
    }

    /**
     * Gets the value of the study property.
     * 
     * @return
     *     possible object is
     *     {@link StudyType }
     *     
     */
    public StudyType getSTUDY() {
        return study;
    }

    /**
     * Sets the value of the study property.
     * 
     * @param value
     *     allowed object is
     *     {@link StudyType }
     *     
     */
    public void setSTUDY(StudyType value) {
        this.study = value;
    }

    /**
     * Gets the value of the sample property.
     * 
     * @return
     *     possible object is
     *     {@link SampleType }
     *     
     */
    public SampleType getSAMPLE() {
        return sample;
    }

    /**
     * Sets the value of the sample property.
     * 
     * @param value
     *     allowed object is
     *     {@link SampleType }
     *     
     */
    public void setSAMPLE(SampleType value) {
        this.sample = value;
    }

    /**
     * Gets the value of the runset property.
     * 
     * @return
     *     possible object is
     *     {@link RunSetType }
     *     
     */
    public RunSetType getRUNSET() {
        return runset;
    }

    /**
     * Sets the value of the runset property.
     * 
     * @param value
     *     allowed object is
     *     {@link RunSetType }
     *     
     */
    public void setRUNSET(RunSetType value) {
        this.runset = value;
    }

}