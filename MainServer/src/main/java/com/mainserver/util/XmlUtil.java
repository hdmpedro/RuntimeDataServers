package com.mainserver.util;

import com.mainserver.model.Carro;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class XmlUtil {
    public static void salvarComoXml(Object objeto, String caminhoArquivo) throws JAXBException, IOException {
        File arquivo = new File(caminhoArquivo);

        // 🔹 Se o diretório não existir, cria ele
        File pasta = arquivo.getParentFile();
        if (pasta != null && !pasta.exists()) {
            boolean criada = pasta.mkdirs();
            System.out.println("Pasta criada? " + criada);
        }

        // 🔹 Teste se o arquivo pode ser criado antes de usar JAXB
        try (FileOutputStream fos = new FileOutputStream(arquivo)) {
            System.out.println("Arquivo criado com sucesso: " + arquivo.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Erro ao criar o arquivo: " + e.getMessage());
            throw e;
        }

        // 🔹 Agora faz a serialização com JAXB
        JAXBContext context = JAXBContext.newInstance(objeto.getClass());
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        try (FileOutputStream fos = new FileOutputStream(arquivo)) {
            marshaller.marshal(objeto, fos);
            System.out.println("Arquivo XML salvo com sucesso.");
        }
    }
}
