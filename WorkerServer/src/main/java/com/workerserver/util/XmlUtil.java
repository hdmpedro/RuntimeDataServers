package com.workerserver.util;

import com.workerserver.model.Carro;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.Marshaller;


import java.io.File;

public class XmlUtil {

    public static Carro lerDeXml(String caminho) throws JAXBException {
        System.setProperty("javax.xml.bind.JAXBContextFactory", "org.glassfish.jaxb.runtime.v2.ContextFactory");

        JAXBContext context = JAXBContext.newInstance(Carro.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (Carro) unmarshaller.unmarshal(new File(caminho));
    }
}
