//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.06.25 at 02:37:37 PM CDT 
//


package com.cerner.ccl.testing.maven.ccl.reports.common.internal.jaxb;

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
 *         &lt;element name="NAME" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="TESTS"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="TEST" maxOccurs="unbounded" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;element name="NAME" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                             &lt;element name="ASSERTS"&gt;
 *                               &lt;complexType&gt;
 *                                 &lt;complexContent&gt;
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                                     &lt;sequence&gt;
 *                                       &lt;element name="ASSERT" maxOccurs="unbounded" minOccurs="0"&gt;
 *                                         &lt;complexType&gt;
 *                                           &lt;complexContent&gt;
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                                               &lt;sequence&gt;
 *                                                 &lt;element name="LINENUMBER" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *                                                 &lt;element name="CONTEXT" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                                                 &lt;element name="RESULT"&gt;
 *                                                   &lt;simpleType&gt;
 *                                                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *                                                       &lt;enumeration value="PASSED"/&gt;
 *                                                       &lt;enumeration value="FAILED"/&gt;
 *                                                     &lt;/restriction&gt;
 *                                                   &lt;/simpleType&gt;
 *                                                 &lt;/element&gt;
 *                                                 &lt;choice&gt;
 *                                                   &lt;element name="TEST" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                                                   &lt;element name="CONDITION" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                                                 &lt;/choice&gt;
 *                                               &lt;/sequence&gt;
 *                                             &lt;/restriction&gt;
 *                                           &lt;/complexContent&gt;
 *                                         &lt;/complexType&gt;
 *                                       &lt;/element&gt;
 *                                     &lt;/sequence&gt;
 *                                   &lt;/restriction&gt;
 *                                 &lt;/complexContent&gt;
 *                               &lt;/complexType&gt;
 *                             &lt;/element&gt;
 *                             &lt;element name="ERRORS"&gt;
 *                               &lt;complexType&gt;
 *                                 &lt;complexContent&gt;
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                                     &lt;sequence&gt;
 *                                       &lt;element name="ERROR" maxOccurs="unbounded" minOccurs="0"&gt;
 *                                         &lt;complexType&gt;
 *                                           &lt;complexContent&gt;
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                                               &lt;sequence&gt;
 *                                                 &lt;element name="LINENUMBER" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *                                                 &lt;element name="ERRORTEXT" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                                               &lt;/sequence&gt;
 *                                             &lt;/restriction&gt;
 *                                           &lt;/complexContent&gt;
 *                                         &lt;/complexType&gt;
 *                                       &lt;/element&gt;
 *                                     &lt;/sequence&gt;
 *                                   &lt;/restriction&gt;
 *                                 &lt;/complexContent&gt;
 *                               &lt;/complexType&gt;
 *                             &lt;/element&gt;
 *                             &lt;element name="RESULT"&gt;
 *                               &lt;simpleType&gt;
 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *                                   &lt;enumeration value="PASSED"/&gt;
 *                                   &lt;enumeration value="FAILED"/&gt;
 *                                   &lt;enumeration value="ERRORED"/&gt;
 *                                 &lt;/restriction&gt;
 *                               &lt;/simpleType&gt;
 *                             &lt;/element&gt;
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
    "name",
    "tests"
})
@XmlRootElement(name = "TESTCASE")
@SuppressWarnings("javadoc") public class TESTCASE {

    @XmlElement(name = "NAME", required = true)
    protected String name;
    @XmlElement(name = "TESTS", required = true)
    protected TESTCASE.TESTS tests;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNAME() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNAME(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the tests property.
     * 
     * @return
     *     possible object is
     *     {@link TESTCASE.TESTS }
     *     
     */
    public TESTCASE.TESTS getTESTS() {
        return tests;
    }

    /**
     * Sets the value of the tests property.
     * 
     * @param value
     *     allowed object is
     *     {@link TESTCASE.TESTS }
     *     
     */
    public void setTESTS(TESTCASE.TESTS value) {
        this.tests = value;
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
     *         &lt;element name="TEST" maxOccurs="unbounded" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;element name="NAME" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *                   &lt;element name="ASSERTS"&gt;
     *                     &lt;complexType&gt;
     *                       &lt;complexContent&gt;
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                           &lt;sequence&gt;
     *                             &lt;element name="ASSERT" maxOccurs="unbounded" minOccurs="0"&gt;
     *                               &lt;complexType&gt;
     *                                 &lt;complexContent&gt;
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                                     &lt;sequence&gt;
     *                                       &lt;element name="LINENUMBER" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
     *                                       &lt;element name="CONTEXT" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *                                       &lt;element name="RESULT"&gt;
     *                                         &lt;simpleType&gt;
     *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
     *                                             &lt;enumeration value="PASSED"/&gt;
     *                                             &lt;enumeration value="FAILED"/&gt;
     *                                           &lt;/restriction&gt;
     *                                         &lt;/simpleType&gt;
     *                                       &lt;/element&gt;
     *                                       &lt;choice&gt;
     *                                         &lt;element name="TEST" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                                         &lt;element name="CONDITION" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                                       &lt;/choice&gt;
     *                                     &lt;/sequence&gt;
     *                                   &lt;/restriction&gt;
     *                                 &lt;/complexContent&gt;
     *                               &lt;/complexType&gt;
     *                             &lt;/element&gt;
     *                           &lt;/sequence&gt;
     *                         &lt;/restriction&gt;
     *                       &lt;/complexContent&gt;
     *                     &lt;/complexType&gt;
     *                   &lt;/element&gt;
     *                   &lt;element name="ERRORS"&gt;
     *                     &lt;complexType&gt;
     *                       &lt;complexContent&gt;
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                           &lt;sequence&gt;
     *                             &lt;element name="ERROR" maxOccurs="unbounded" minOccurs="0"&gt;
     *                               &lt;complexType&gt;
     *                                 &lt;complexContent&gt;
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                                     &lt;sequence&gt;
     *                                       &lt;element name="LINENUMBER" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
     *                                       &lt;element name="ERRORTEXT" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *                                     &lt;/sequence&gt;
     *                                   &lt;/restriction&gt;
     *                                 &lt;/complexContent&gt;
     *                               &lt;/complexType&gt;
     *                             &lt;/element&gt;
     *                           &lt;/sequence&gt;
     *                         &lt;/restriction&gt;
     *                       &lt;/complexContent&gt;
     *                     &lt;/complexType&gt;
     *                   &lt;/element&gt;
     *                   &lt;element name="RESULT"&gt;
     *                     &lt;simpleType&gt;
     *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
     *                         &lt;enumeration value="PASSED"/&gt;
     *                         &lt;enumeration value="FAILED"/&gt;
     *                         &lt;enumeration value="ERRORED"/&gt;
     *                       &lt;/restriction&gt;
     *                     &lt;/simpleType&gt;
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
        "test"
    })
    public static class TESTS {

        @XmlElement(name = "TEST")
        protected List<TESTCASE.TESTS.TEST> test;

        /**
         * Gets the value of the test property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the test property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getTEST().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TESTCASE.TESTS.TEST }
         * 
         * 
         */
        public List<TESTCASE.TESTS.TEST> getTEST() {
            if (test == null) {
                test = new ArrayList<TESTCASE.TESTS.TEST>();
            }
            return this.test;
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
         *         &lt;element name="NAME" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
         *         &lt;element name="ASSERTS"&gt;
         *           &lt;complexType&gt;
         *             &lt;complexContent&gt;
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *                 &lt;sequence&gt;
         *                   &lt;element name="ASSERT" maxOccurs="unbounded" minOccurs="0"&gt;
         *                     &lt;complexType&gt;
         *                       &lt;complexContent&gt;
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *                           &lt;sequence&gt;
         *                             &lt;element name="LINENUMBER" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
         *                             &lt;element name="CONTEXT" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
         *                             &lt;element name="RESULT"&gt;
         *                               &lt;simpleType&gt;
         *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
         *                                   &lt;enumeration value="PASSED"/&gt;
         *                                   &lt;enumeration value="FAILED"/&gt;
         *                                 &lt;/restriction&gt;
         *                               &lt;/simpleType&gt;
         *                             &lt;/element&gt;
         *                             &lt;choice&gt;
         *                               &lt;element name="TEST" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *                               &lt;element name="CONDITION" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
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
         *         &lt;element name="ERRORS"&gt;
         *           &lt;complexType&gt;
         *             &lt;complexContent&gt;
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *                 &lt;sequence&gt;
         *                   &lt;element name="ERROR" maxOccurs="unbounded" minOccurs="0"&gt;
         *                     &lt;complexType&gt;
         *                       &lt;complexContent&gt;
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *                           &lt;sequence&gt;
         *                             &lt;element name="LINENUMBER" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
         *                             &lt;element name="ERRORTEXT" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
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
         *         &lt;element name="RESULT"&gt;
         *           &lt;simpleType&gt;
         *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
         *               &lt;enumeration value="PASSED"/&gt;
         *               &lt;enumeration value="FAILED"/&gt;
         *               &lt;enumeration value="ERRORED"/&gt;
         *             &lt;/restriction&gt;
         *           &lt;/simpleType&gt;
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
            "name",
            "asserts",
            "errors",
            "result"
        })
        public static class TEST {

            @XmlElement(name = "NAME", required = true)
            protected String name;
            @XmlElement(name = "ASSERTS", required = true)
            protected TESTCASE.TESTS.TEST.ASSERTS asserts;
            @XmlElement(name = "ERRORS", required = true)
            protected TESTCASE.TESTS.TEST.ERRORS errors;
            @XmlElement(name = "RESULT", required = true)
            protected String result;

            /**
             * Gets the value of the name property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getNAME() {
                return name;
            }

            /**
             * Sets the value of the name property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setNAME(String value) {
                this.name = value;
            }

            /**
             * Gets the value of the asserts property.
             * 
             * @return
             *     possible object is
             *     {@link TESTCASE.TESTS.TEST.ASSERTS }
             *     
             */
            public TESTCASE.TESTS.TEST.ASSERTS getASSERTS() {
                return asserts;
            }

            /**
             * Sets the value of the asserts property.
             * 
             * @param value
             *     allowed object is
             *     {@link TESTCASE.TESTS.TEST.ASSERTS }
             *     
             */
            public void setASSERTS(TESTCASE.TESTS.TEST.ASSERTS value) {
                this.asserts = value;
            }

            /**
             * Gets the value of the errors property.
             * 
             * @return
             *     possible object is
             *     {@link TESTCASE.TESTS.TEST.ERRORS }
             *     
             */
            public TESTCASE.TESTS.TEST.ERRORS getERRORS() {
                return errors;
            }

            /**
             * Sets the value of the errors property.
             * 
             * @param value
             *     allowed object is
             *     {@link TESTCASE.TESTS.TEST.ERRORS }
             *     
             */
            public void setERRORS(TESTCASE.TESTS.TEST.ERRORS value) {
                this.errors = value;
            }

            /**
             * Gets the value of the result property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getRESULT() {
                return result;
            }

            /**
             * Sets the value of the result property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setRESULT(String value) {
                this.result = value;
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
             *         &lt;element name="ASSERT" maxOccurs="unbounded" minOccurs="0"&gt;
             *           &lt;complexType&gt;
             *             &lt;complexContent&gt;
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
             *                 &lt;sequence&gt;
             *                   &lt;element name="LINENUMBER" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
             *                   &lt;element name="CONTEXT" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
             *                   &lt;element name="RESULT"&gt;
             *                     &lt;simpleType&gt;
             *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
             *                         &lt;enumeration value="PASSED"/&gt;
             *                         &lt;enumeration value="FAILED"/&gt;
             *                       &lt;/restriction&gt;
             *                     &lt;/simpleType&gt;
             *                   &lt;/element&gt;
             *                   &lt;choice&gt;
             *                     &lt;element name="TEST" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
             *                     &lt;element name="CONDITION" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
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
                "_assert"
            })
            public static class ASSERTS {

                @XmlElement(name = "ASSERT")
                protected List<TESTCASE.TESTS.TEST.ASSERTS.ASSERT> _assert;

                /**
                 * Gets the value of the assert property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the assert property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getASSERT().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link TESTCASE.TESTS.TEST.ASSERTS.ASSERT }
                 * 
                 * 
                 */
                public List<TESTCASE.TESTS.TEST.ASSERTS.ASSERT> getASSERT() {
                    if (_assert == null) {
                        _assert = new ArrayList<TESTCASE.TESTS.TEST.ASSERTS.ASSERT>();
                    }
                    return this._assert;
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
                 *         &lt;element name="LINENUMBER" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
                 *         &lt;element name="CONTEXT" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
                 *         &lt;element name="RESULT"&gt;
                 *           &lt;simpleType&gt;
                 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                 *               &lt;enumeration value="PASSED"/&gt;
                 *               &lt;enumeration value="FAILED"/&gt;
                 *             &lt;/restriction&gt;
                 *           &lt;/simpleType&gt;
                 *         &lt;/element&gt;
                 *         &lt;choice&gt;
                 *           &lt;element name="TEST" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
                 *           &lt;element name="CONDITION" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
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
                    "linenumber",
                    "context",
                    "result",
                    "test",
                    "condition"
                })
                public static class ASSERT {

                    @XmlElement(name = "LINENUMBER")
                    protected long linenumber;
                    @XmlElement(name = "CONTEXT", required = true)
                    protected String context;
                    @XmlElement(name = "RESULT", required = true)
                    protected String result;
                    @XmlElement(name = "TEST")
                    protected String test;
                    @XmlElement(name = "CONDITION")
                    protected String condition;

                    /**
                     * Gets the value of the linenumber property.
                     * 
                     */
                    public long getLINENUMBER() {
                        return linenumber;
                    }

                    /**
                     * Sets the value of the linenumber property.
                     * 
                     */
                    public void setLINENUMBER(long value) {
                        this.linenumber = value;
                    }

                    /**
                     * Gets the value of the context property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getCONTEXT() {
                        return context;
                    }

                    /**
                     * Sets the value of the context property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setCONTEXT(String value) {
                        this.context = value;
                    }

                    /**
                     * Gets the value of the result property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getRESULT() {
                        return result;
                    }

                    /**
                     * Sets the value of the result property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setRESULT(String value) {
                        this.result = value;
                    }

                    /**
                     * Gets the value of the test property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getTEST() {
                        return test;
                    }

                    /**
                     * Sets the value of the test property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setTEST(String value) {
                        this.test = value;
                    }

                    /**
                     * Gets the value of the condition property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getCONDITION() {
                        return condition;
                    }

                    /**
                     * Sets the value of the condition property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setCONDITION(String value) {
                        this.condition = value;
                    }

                }

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
             *         &lt;element name="ERROR" maxOccurs="unbounded" minOccurs="0"&gt;
             *           &lt;complexType&gt;
             *             &lt;complexContent&gt;
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
             *                 &lt;sequence&gt;
             *                   &lt;element name="LINENUMBER" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
             *                   &lt;element name="ERRORTEXT" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
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
                "error"
            })
            public static class ERRORS {

                @XmlElement(name = "ERROR")
                protected List<TESTCASE.TESTS.TEST.ERRORS.ERROR> error;

                /**
                 * Gets the value of the error property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the error property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getERROR().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link TESTCASE.TESTS.TEST.ERRORS.ERROR }
                 * 
                 * 
                 */
                public List<TESTCASE.TESTS.TEST.ERRORS.ERROR> getERROR() {
                    if (error == null) {
                        error = new ArrayList<TESTCASE.TESTS.TEST.ERRORS.ERROR>();
                    }
                    return this.error;
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
                 *         &lt;element name="LINENUMBER" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
                 *         &lt;element name="ERRORTEXT" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
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
                    "linenumber",
                    "errortext"
                })
                public static class ERROR {

                    @XmlElement(name = "LINENUMBER")
                    protected long linenumber;
                    @XmlElement(name = "ERRORTEXT", required = true)
                    protected String errortext;

                    /**
                     * Gets the value of the linenumber property.
                     * 
                     */
                    public long getLINENUMBER() {
                        return linenumber;
                    }

                    /**
                     * Sets the value of the linenumber property.
                     * 
                     */
                    public void setLINENUMBER(long value) {
                        this.linenumber = value;
                    }

                    /**
                     * Gets the value of the errortext property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getERRORTEXT() {
                        return errortext;
                    }

                    /**
                     * Sets the value of the errortext property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setERRORTEXT(String value) {
                        this.errortext = value;
                    }

                }

            }

        }

    }

}
