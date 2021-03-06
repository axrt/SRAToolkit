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
 * 
 *                A SRA package is a container for any combination of SRA objects.  This object is
 *                useful to serve as the document root  for a collection of SRA objects retrieved in a search,
 *                or delivered together in a submission.  A package may also contain other packages.
 *             
 * 
 * <p>Java class for PackageType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PackageType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="SUBMISSION_SET" type="{}SubmissionSetType"/>
 *         &lt;element name="SUBMISSION" type="{}SubmissionType"/>
 *         &lt;element name="STUDY_SET" type="{}StudySetType"/>
 *         &lt;element name="STUDY" type="{}StudyType"/>
 *         &lt;element name="SAMPLE_SET" type="{}SampleSetType"/>
 *         &lt;element name="SAMPLE" type="{}SampleType"/>
 *         &lt;element name="EXPERIMENT_SET" type="{}ExperimentSetType"/>
 *         &lt;element name="EXPERIMENT" type="{}ExperimentType"/>
 *         &lt;element name="RUN_SET" type="{}RunSetType"/>
 *         &lt;element name="RUN" type="{}RunType"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PackageType", propOrder = {

})
public class PackageType {

    @XmlElement(name = "SUBMISSION_SET", required = true)
    protected SubmissionSetType submissionset;
    @XmlElement(name = "SUBMISSION", required = true)
    protected SubmissionType submission;
    @XmlElement(name = "STUDY_SET", required = true)
    protected StudySetType studyset;
    @XmlElement(name = "STUDY", required = true)
    protected StudyType study;
    @XmlElement(name = "SAMPLE_SET", required = true)
    protected SampleSetType sampleset;
    @XmlElement(name = "SAMPLE", required = true)
    protected SampleType sample;
    @XmlElement(name = "EXPERIMENT_SET", required = true)
    protected ExperimentSetType experimentset;
    @XmlElement(name = "EXPERIMENT", required = true)
    protected ExperimentType experiment;
    @XmlElement(name = "RUN_SET", required = true)
    protected RunSetType runset;
    @XmlElement(name = "RUN", required = true)
    protected RunType run;

    /**
     * Gets the value of the submissionset property.
     * 
     * @return
     *     possible object is
     *     {@link SubmissionSetType }
     *     
     */
    public SubmissionSetType getSUBMISSIONSET() {
        return submissionset;
    }

    /**
     * Sets the value of the submissionset property.
     * 
     * @param value
     *     allowed object is
     *     {@link SubmissionSetType }
     *     
     */
    public void setSUBMISSIONSET(SubmissionSetType value) {
        this.submissionset = value;
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
     * Gets the value of the studyset property.
     * 
     * @return
     *     possible object is
     *     {@link StudySetType }
     *     
     */
    public StudySetType getSTUDYSET() {
        return studyset;
    }

    /**
     * Sets the value of the studyset property.
     * 
     * @param value
     *     allowed object is
     *     {@link StudySetType }
     *     
     */
    public void setSTUDYSET(StudySetType value) {
        this.studyset = value;
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
     * Gets the value of the sampleset property.
     * 
     * @return
     *     possible object is
     *     {@link SampleSetType }
     *     
     */
    public SampleSetType getSAMPLESET() {
        return sampleset;
    }

    /**
     * Sets the value of the sampleset property.
     * 
     * @param value
     *     allowed object is
     *     {@link SampleSetType }
     *     
     */
    public void setSAMPLESET(SampleSetType value) {
        this.sampleset = value;
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
     * Gets the value of the experimentset property.
     * 
     * @return
     *     possible object is
     *     {@link ExperimentSetType }
     *     
     */
    public ExperimentSetType getEXPERIMENTSET() {
        return experimentset;
    }

    /**
     * Sets the value of the experimentset property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExperimentSetType }
     *     
     */
    public void setEXPERIMENTSET(ExperimentSetType value) {
        this.experimentset = value;
    }

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

    /**
     * Gets the value of the run property.
     * 
     * @return
     *     possible object is
     *     {@link RunType }
     *     
     */
    public RunType getRUN() {
        return run;
    }

    /**
     * Sets the value of the run property.
     * 
     * @param value
     *     allowed object is
     *     {@link RunType }
     *     
     */
    public void setRUN(RunType value) {
        this.run = value;
    }

}
