//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.06.19 at 07:12:58 PM IST 
//


package com.hanover.bordereaux.xsdoutput;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.hanover.bordereaux.xsdoutput package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ExpenseReport_QNAME = new QName("", "expenseReport");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.hanover.bordereaux.xsdoutput
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ExpenseT }
     * 
     */
    public ExpenseT createExpenseT() {
        return new ExpenseT();
    }

    /**
     * Create an instance of {@link ItemListT }
     * 
     */
    public ItemListT createItemListT() {
        return new ItemListT();
    }

    /**
     * Create an instance of {@link ItemT }
     * 
     */
    public ItemT createItemT() {
        return new ItemT();
    }

    /**
     * Create an instance of {@link UserT }
     * 
     */
    public UserT createUserT() {
        return new UserT();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExpenseT }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "expenseReport")
    public JAXBElement<ExpenseT> createExpenseReport(ExpenseT value) {
        return new JAXBElement<ExpenseT>(_ExpenseReport_QNAME, ExpenseT.class, null, value);
    }

}
