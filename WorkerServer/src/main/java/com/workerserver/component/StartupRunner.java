package com.workerserver.component;

import com.workerserver.model.Carro;
import com.workerserver.util.XmlUtil;
import jakarta.xml.bind.JAXBException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


import java.io.File;

@Component
public class StartupRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        String dataDir = System.getProperty("data.dir");
        if (dataDir == null) {
            dataDir = "worker_data";
        }

        File dir = new File(dataDir);
        if (dir.exists() && dir.isDirectory()) {
            File[] xmlFiles = dir.listFiles((d, name) -> name.endsWith(".xml"));
            if (xmlFiles != null) {
                for (File xmlFile : xmlFiles) {
                    Carro carro = XmlUtil.lerDeXml(xmlFile.getAbsolutePath());
                    System.out.println("Carro carregado: " + carro);
                }
            }
        }
    }
}

