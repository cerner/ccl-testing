//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.06.13 at 04:35:46 PM CDT 
//


package com.cerner.ccl.testing.maven.ccl.reports.common.internal.jaxb;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CURNODE" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="CURDOMAIN" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="CURRDB" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="CURRDBNAME" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="CURRDBUSER" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="CURRDBSYS" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="CCLVER" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="CURSYS" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="CURSYS2" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="CURLOCALE" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="CURUSER" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="CURUTC" type="{http://www.w3.org/2001/XMLSchema}integer"/&gt;
 *         &lt;element name="CURUTCDIFF" type="{http://www.w3.org/2001/XMLSchema}integer"/&gt;
 *         &lt;element name="CURTIMEZONE" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="CURTIMEZONEAPP" type="{http://www.w3.org/2001/XMLSchema}integer"/&gt;
 *         &lt;element name="CURTIMEZONESYS" type="{http://www.w3.org/2001/XMLSchema}integer"/&gt;
 *         &lt;element name="CURREVAFD" type="{http://www.w3.org/2001/XMLSchema}integer"/&gt;
 *         &lt;element name="CURGROUP" type="{http://www.w3.org/2001/XMLSchema}integer"/&gt;
 *         &lt;element name="DBVERSION" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="DBOPTMODE" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="CCLUSER" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="FRAMEWORK_VERSION" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="REQUIRED_CCL" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "curnode",
    "curdomain",
    "currdb",
    "currdbname",
    "currdbuser",
    "currdbsys",
    "cclver",
    "cursys",
    "cursys2",
    "curlocale",
    "curuser",
    "curutc",
    "curutcdiff",
    "curtimezone",
    "curtimezoneapp",
    "curtimezonesys",
    "currevafd",
    "curgroup",
    "dbversion",
    "dboptmode",
    "ccluser",
    "frameworkversion",
    "requiredccl"
})
@XmlRootElement(name = "ENVIRONMENT")
@SuppressWarnings("javadoc") public class ENVIRONMENT {

    @XmlElement(name = "CURNODE", required = true)
    protected String curnode;
    @XmlElement(name = "CURDOMAIN", required = true)
    protected String curdomain;
    @XmlElement(name = "CURRDB", required = true)
    protected String currdb;
    @XmlElement(name = "CURRDBNAME", required = true)
    protected String currdbname;
    @XmlElement(name = "CURRDBUSER", required = true)
    protected String currdbuser;
    @XmlElement(name = "CURRDBSYS", required = true)
    protected String currdbsys;
    @XmlElement(name = "CCLVER", required = true)
    protected String cclver;
    @XmlElement(name = "CURSYS", required = true)
    protected String cursys;
    @XmlElement(name = "CURSYS2", required = true)
    protected String cursys2;
    @XmlElement(name = "CURLOCALE", required = true)
    protected String curlocale;
    @XmlElement(name = "CURUSER", required = true)
    protected String curuser;
    @XmlElement(name = "CURUTC", required = true)
    protected BigInteger curutc;
    @XmlElement(name = "CURUTCDIFF", required = true)
    protected BigInteger curutcdiff;
    @XmlElement(name = "CURTIMEZONE", required = true)
    protected String curtimezone;
    @XmlElement(name = "CURTIMEZONEAPP", required = true)
    protected BigInteger curtimezoneapp;
    @XmlElement(name = "CURTIMEZONESYS", required = true)
    protected BigInteger curtimezonesys;
    @XmlElement(name = "CURREVAFD", required = true)
    protected BigInteger currevafd;
    @XmlElement(name = "CURGROUP", required = true)
    protected BigInteger curgroup;
    @XmlElement(name = "DBVERSION", required = true)
    protected String dbversion;
    @XmlElement(name = "DBOPTMODE", required = true)
    protected String dboptmode;
    @XmlElement(name = "CCLUSER", required = true)
    protected String ccluser;
    @XmlElement(name = "FRAMEWORK_VERSION", required = true)
    protected String frameworkversion;
    @XmlElement(name = "REQUIRED_CCL", required = true)
    protected String requiredccl;

    /**
     * Gets the value of the curnode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCURNODE() {
        return curnode;
    }

    /**
     * Sets the value of the curnode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCURNODE(String value) {
        this.curnode = value;
    }

    /**
     * Gets the value of the curdomain property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCURDOMAIN() {
        return curdomain;
    }

    /**
     * Sets the value of the curdomain property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCURDOMAIN(String value) {
        this.curdomain = value;
    }

    /**
     * Gets the value of the currdb property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCURRDB() {
        return currdb;
    }

    /**
     * Sets the value of the currdb property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCURRDB(String value) {
        this.currdb = value;
    }

    /**
     * Gets the value of the currdbname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCURRDBNAME() {
        return currdbname;
    }

    /**
     * Sets the value of the currdbname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCURRDBNAME(String value) {
        this.currdbname = value;
    }

    /**
     * Gets the value of the currdbuser property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCURRDBUSER() {
        return currdbuser;
    }

    /**
     * Sets the value of the currdbuser property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCURRDBUSER(String value) {
        this.currdbuser = value;
    }

    /**
     * Gets the value of the currdbsys property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCURRDBSYS() {
        return currdbsys;
    }

    /**
     * Sets the value of the currdbsys property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCURRDBSYS(String value) {
        this.currdbsys = value;
    }

    /**
     * Gets the value of the cclver property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCCLVER() {
        return cclver;
    }

    /**
     * Sets the value of the cclver property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCCLVER(String value) {
        this.cclver = value;
    }

    /**
     * Gets the value of the cursys property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCURSYS() {
        return cursys;
    }

    /**
     * Sets the value of the cursys property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCURSYS(String value) {
        this.cursys = value;
    }

    /**
     * Gets the value of the cursys2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCURSYS2() {
        return cursys2;
    }

    /**
     * Sets the value of the cursys2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCURSYS2(String value) {
        this.cursys2 = value;
    }

    /**
     * Gets the value of the curlocale property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCURLOCALE() {
        return curlocale;
    }

    /**
     * Sets the value of the curlocale property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCURLOCALE(String value) {
        this.curlocale = value;
    }

    /**
     * Gets the value of the curuser property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCURUSER() {
        return curuser;
    }

    /**
     * Sets the value of the curuser property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCURUSER(String value) {
        this.curuser = value;
    }

    /**
     * Gets the value of the curutc property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCURUTC() {
        return curutc;
    }

    /**
     * Sets the value of the curutc property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCURUTC(BigInteger value) {
        this.curutc = value;
    }

    /**
     * Gets the value of the curutcdiff property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCURUTCDIFF() {
        return curutcdiff;
    }

    /**
     * Sets the value of the curutcdiff property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCURUTCDIFF(BigInteger value) {
        this.curutcdiff = value;
    }

    /**
     * Gets the value of the curtimezone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCURTIMEZONE() {
        return curtimezone;
    }

    /**
     * Sets the value of the curtimezone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCURTIMEZONE(String value) {
        this.curtimezone = value;
    }

    /**
     * Gets the value of the curtimezoneapp property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCURTIMEZONEAPP() {
        return curtimezoneapp;
    }

    /**
     * Sets the value of the curtimezoneapp property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCURTIMEZONEAPP(BigInteger value) {
        this.curtimezoneapp = value;
    }

    /**
     * Gets the value of the curtimezonesys property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCURTIMEZONESYS() {
        return curtimezonesys;
    }

    /**
     * Sets the value of the curtimezonesys property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCURTIMEZONESYS(BigInteger value) {
        this.curtimezonesys = value;
    }

    /**
     * Gets the value of the currevafd property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCURREVAFD() {
        return currevafd;
    }

    /**
     * Sets the value of the currevafd property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCURREVAFD(BigInteger value) {
        this.currevafd = value;
    }

    /**
     * Gets the value of the curgroup property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCURGROUP() {
        return curgroup;
    }

    /**
     * Sets the value of the curgroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCURGROUP(BigInteger value) {
        this.curgroup = value;
    }

    /**
     * Gets the value of the dbversion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDBVERSION() {
        return dbversion;
    }

    /**
     * Sets the value of the dbversion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDBVERSION(String value) {
        this.dbversion = value;
    }

    /**
     * Gets the value of the dboptmode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDBOPTMODE() {
        return dboptmode;
    }

    /**
     * Sets the value of the dboptmode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDBOPTMODE(String value) {
        this.dboptmode = value;
    }

    /**
     * Gets the value of the ccluser property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCCLUSER() {
        return ccluser;
    }

    /**
     * Sets the value of the ccluser property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCCLUSER(String value) {
        this.ccluser = value;
    }

    /**
     * Gets the value of the frameworkversion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFRAMEWORKVERSION() {
        return frameworkversion;
    }

    /**
     * Sets the value of the frameworkversion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFRAMEWORKVERSION(String value) {
        this.frameworkversion = value;
    }

    /**
     * Gets the value of the requiredccl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getREQUIREDCCL() {
        return requiredccl;
    }

    /**
     * Sets the value of the requiredccl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setREQUIREDCCL(String value) {
        this.requiredccl = value;
    }

}
