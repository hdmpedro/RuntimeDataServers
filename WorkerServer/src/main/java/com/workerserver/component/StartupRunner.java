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
        // Recupera a pasta de dados definida como propriedade de sistema
        String dataDir = System.getProperty("data.dir");
        if (dataDir == null) {
            dataDir = "worker_data"; // padrão
        }
        File xmlFile = new File(dataDir, "carro.xml");
        if (xmlFile.exists()) {
            Carro carro = XmlUtil.lerDeXml(xmlFile.getAbsolutePath());
            System.out.println("Carro carregado a partir do XML: " + carro);
            // Aqui você pode armazenar o objeto ou usá-lo conforme necessário
        } else {
            System.out.println("Nenhum arquivo XML encontrado em " + dataDir);
        }
    }
}
