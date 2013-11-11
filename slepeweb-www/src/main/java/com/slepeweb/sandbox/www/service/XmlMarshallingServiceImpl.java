package com.slepeweb.sandbox.www.service;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service("xmlMarshallingService")
public class XmlMarshallingServiceImpl implements XmlMarshallingService {
	private static Logger LOG = Logger.getLogger(XmlMarshallingServiceImpl.class);

	public Object unmarshall(String payload, Object obj) {
		try {
			JAXBContext jc = JAXBContext.newInstance(obj.getClass());
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			return unmarshaller.unmarshal(new StringReader(payload));
		} catch (Exception e) {
			LOG.error("Failed to unmarshall the object [" + obj.getClass().getName() + "]", e);
		}
		return null;
	}
	
	public String marshall(Object obj) {
		// TODO: do something with this, from mkyong
		
//		JAXBContext jaxbContext = JAXBContext.newInstance(Customer.class);
//		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
// 
//		// output pretty printed
//		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
// 
//		jaxbMarshaller.marshal(customer, file);
//		jaxbMarshaller.marshal(customer, System.out);
		
		return null;
	}
}
