//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.06.25 at 02:37:37 PM CDT 
//


package com.cerner.ccl.testing.maven.ccl.reports.common.internal.jaxb;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
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
 *         &lt;element name="LISTING_NAME" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="COMPILE_DATE" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="LINES"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="LINE" maxOccurs="unbounded"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;element name="NBR" type="{http://www.w3.org/2001/XMLSchema}integer"/&gt;
 *                             &lt;element name="TEXT" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                             &lt;choice minOccurs="0"&gt;
 *                               &lt;element name="START_OF_INC" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                               &lt;element name="END_OF_INC" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                             &lt;/choice&gt;
 *                           &lt;/sequence&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
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
    "listingname",
    "compiledate",
    "lines"
})
@XmlRootElement(name = "LISTING")
@SuppressWarnings("javadoc") public class LISTING {

    @XmlElement(name = "LISTING_NAME", required = true)
    protected String listingname;
    @XmlElement(name = "COMPILE_DATE", required = true)
    protected String compiledate;
    @XmlElement(name = "LINES", required = true)
    protected LISTING.LINES lines;

    /**
     * Gets the value of the listingname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLISTINGNAME() {
        return listingname;
    }

    /**
     * Sets the value of the listingname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLISTINGNAME(String value) {
        this.listingname = value;
    }

    /**
     * Gets the value of the compiledate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCOMPILEDATE() {
        return compiledate;
    }

    /**
     * Sets the value of the compiledate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCOMPILEDATE(String value) {
        this.compiledate = value;
    }

    /**
     * Gets the value of the lines property.
     * 
     * @return
     *     possible object is
     *     {@link LISTING.LINES }
     *     
     */
    public LISTING.LINES getLINES() {
        return lines;
    }

    /**
     * Sets the value of the lines property.
     * 
     * @param value
     *     allowed object is
     *     {@link LISTING.LINES }
     *     
     */
    public void setLINES(LISTING.LINES value) {
        this.lines = value;
    }


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
     *         &lt;element name="LINE" maxOccurs="unbounded"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;element name="NBR" type="{http://www.w3.org/2001/XMLSchema}integer"/&gt;
     *                   &lt;element name="TEXT" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *                   &lt;choice minOccurs="0"&gt;
     *                     &lt;element name="START_OF_INC" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *                     &lt;element name="END_OF_INC" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *                   &lt;/choice&gt;
     *                 &lt;/sequence&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
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
        "line"
    })
    public static class LINES {

        @XmlElement(name = "LINE", required = true)
        protected List<LISTING.LINES.LINE> line;

        /**
         * Gets the value of the line property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the line property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getLINE().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link LISTING.LINES.LINE }
         * 
         * 
         */
        public List<LISTING.LINES.LINE> getLINE() {
            if (line == null) {
                line = new ArrayList<LISTING.LINES.LINE>();
            }
            return this.line;
        }


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
         *         &lt;element name="NBR" type="{http://www.w3.org/2001/XMLSchema}integer"/&gt;
         *         &lt;element name="TEXT" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
         *         &lt;choice minOccurs="0"&gt;
         *           &lt;element name="START_OF_INC" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
         *           &lt;element name="END_OF_INC" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
         *         &lt;/choice&gt;
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
            "nbr",
            "text",
            "startofinc",
            "endofinc"
        })
        public static class LINE {

            @XmlElement(name = "NBR", required = true)
            protected BigInteger nbr;
            @XmlElement(name = "TEXT", required = true)
            protected String text;
            @XmlElement(name = "START_OF_INC")
            protected String startofinc;
            @XmlElement(name = "END_OF_INC")
            protected String endofinc;

            /**
             * Gets the value of the nbr property.
             * 
             * @return
             *     possible object is
             *     {@link BigInteger }
             *     
             */
            public BigInteger getNBR() {
                return nbr;
            }

            /**
             * Sets the value of the nbr property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigInteger }
             *     
             */
            public void setNBR(BigInteger value) {
                this.nbr = value;
            }

            /**
             * Gets the value of the text property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getTEXT() {
                return text;
            }

            /**
             * Sets the value of the text property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setTEXT(String value) {
                this.text = value;
            }

            /**
             * Gets the value of the startofinc property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getSTARTOFINC() {
                return startofinc;
            }

            /**
             * Sets the value of the startofinc property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setSTARTOFINC(String value) {
                this.startofinc = value;
            }

            /**
             * Gets the value of the endofinc property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getENDOFINC() {
                return endofinc;
            }

            /**
             * Sets the value of the endofinc property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setENDOFINC(String value) {
                this.endofinc = value;
            }

        }

    }

}
