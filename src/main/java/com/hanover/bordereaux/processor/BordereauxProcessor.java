package com.hanover.bordereaux.processor;

import java.io.File;
import java.io.StringWriter;
import java.math.BigDecimal;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.hanover.bordereaux.modal.User;
import com.hanover.bordereaux.xsdoutput.ExpenseT;
import com.hanover.bordereaux.xsdoutput.ItemListT;
import com.hanover.bordereaux.xsdoutput.ItemT;
import com.hanover.bordereaux.xsdoutput.ObjectFactory;
import com.hanover.bordereaux.xsdoutput.UserT;
/**
 * 
 * @author CTS
 *
 */
@Component
public class BordereauxProcessor implements ItemProcessor<User, User> {
	private String threadName;
	@Autowired
	private Environment environment;
	private String test;

	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	@Override
	public User process(User item) throws Exception {

		RestTemplate restTemp = new RestTemplate();
		String restServiceOutput = restTemp.getForObject(environment.getProperty("serviceproviderurl") + item.getId(),
				String.class);
		System.out.println(" UserProcessor getting String  from  External Service :  " + restServiceOutput);
		item.setPassword(restServiceOutput);
		// jaxbObjectToXML(item);
		xsdToXml(item);
		return item;
	}

	private static void jaxbObjectToXML(User item) {

		try {
			JAXBContext context = JAXBContext.newInstance(User.class);
			Marshaller m = context.createMarshaller();
			// for pretty-print XML in JAXB
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			File file = new File("xml/jaxb-User" + item.getId() + ".xml");
			file.getParentFile().mkdirs();
			// Write to System.out for debugging
			m.marshal(item, System.out);
			// Write to File
			m.marshal(item, file);

		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	private static void xsdToXml(User item) {

		try {
			ObjectFactory factory = new ObjectFactory();

			UserT user = factory.createUserT();
			user.setUserName(item.getUsername());
			ItemT itemT = factory.createItemT();
			itemT.setItemName(item.getId() + "ItemName");
			itemT.setPurchasedOn(item.getId() + "ItemName");
			itemT.setAmount(new BigDecimal(item.getAge()));

			ItemListT itemList = factory.createItemListT();
			itemList.getItem().add(itemT);

			ExpenseT expense = factory.createExpenseT();
			expense.setUser(user);
			expense.setItems(itemList);

			JAXBContext context = JAXBContext.newInstance("com.hanover.bordereaux.xsdoutput");
			JAXBElement<ExpenseT> element = factory.createExpenseReport(expense);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
			// marshaller.marshal(element, System.out);

			StringWriter sb = new StringWriter();
			marshaller.marshal(element, sb);
			System.out.println(" Output  XML : ");
			System.out.println(sb.toString());

			File file = new File("XSDxml/jaxb-User" + item.getId() + ".xml");
			file.getParentFile().mkdirs();
			marshaller.marshal(element, file);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
}
