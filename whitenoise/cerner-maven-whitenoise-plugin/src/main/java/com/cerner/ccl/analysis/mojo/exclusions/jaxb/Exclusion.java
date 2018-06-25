//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.06.25 at 02:38:34 PM CDT 
//


package com.cerner.ccl.analysis.mojo.exclusions.jaxb;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for exclusion complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="exclusion"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="scriptName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="violationId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="subroutineName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="variableName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="lineNumber" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/&gt;
 *         &lt;element name="explanation" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "exclusion", namespace = "urn:cerner:ccl:whitenoise-exclusions", propOrder = {
    "scriptName",
    "violationId",
    "subroutineName",
    "variableName",
    "lineNumber",
    "explanation"
})
@SuppressWarnings("javadoc") public class Exclusion {

    @XmlElement(namespace = "urn:cerner:ccl:whitenoise-exclusions")
    protected String scriptName;
    @XmlElement(namespace = "urn:cerner:ccl:whitenoise-exclusions")
    protected String violationId;
    @XmlElement(namespace = "urn:cerner:ccl:whitenoise-exclusions")
    protected String subroutineName;
    @XmlElement(namespace = "urn:cerner:ccl:whitenoise-exclusions")
    protected String variableName;
    @XmlElement(namespace = "urn:cerner:ccl:whitenoise-exclusions")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger lineNumber;
    @XmlElement(namespace = "urn:cerner:ccl:whitenoise-exclusions")
    protected String explanation;

    /**
     * Gets the value of the scriptName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getScriptName() {
        return scriptName;
    }

    /**
     * Sets the value of the scriptName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setScriptName(String value) {
        this.scriptName = value;
    }

    /**
     * Gets the value of the violationId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getViolationId() {
        return violationId;
    }

    /**
     * Sets the value of the violationId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setViolationId(String value) {
        this.violationId = value;
    }

    /**
     * Gets the value of the subroutineName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubroutineName() {
        return subroutineName;
    }

    /**
     * Sets the value of the subroutineName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubroutineName(String value) {
        this.subroutineName = value;
    }

    /**
     * Gets the value of the variableName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVariableName() {
        return variableName;
    }

    /**
     * Sets the value of the variableName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVariableName(String value) {
        this.variableName = value;
    }

    /**
     * Gets the value of the lineNumber property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getLineNumber() {
        return lineNumber;
    }

    /**
     * Sets the value of the lineNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setLineNumber(BigInteger value) {
        this.lineNumber = value;
    }

    /**
     * Gets the value of the explanation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExplanation() {
        return explanation;
    }

    /**
     * Sets the value of the explanation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExplanation(String value) {
        this.explanation = value;
    }

}
