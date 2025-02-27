package com.mainserver.controller;

import com.mainserver.model.Carro;
import com.mainserver.service.WorkerManager;
import com.mainserver.util.XmlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/worker")
public class WorkerController {

    private final WorkerManager workerManager;
    private final RestTemplate restTemplate = new RestTemplate();
    private final String DATA_DIR = "worker_data"; // Mesmo definido no WorkerManager
    private final String XML_PATH = DATA_DIR + "/carro.xml";

    public boolean waitForWorkerServer(String url, int timeoutMillis) {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < timeoutMillis) {
            try {
                // Tenta uma requisição GET simples para o endpoint (ou HEAD)
                ResponseEntity<String> response = new RestTemplate().getForEntity(url, String.class);
                if (response.getStatusCode().is2xxSuccessful()) {
                    return true;
                }
            } catch (Exception e) {
                // Aguarda um pouco e tenta novamente
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return false;
    }


    @Autowired
    public WorkerController(WorkerManager workerManager) {
        this.workerManager = workerManager;
    }

    @PostMapping("/start")
    public ResponseEntity<String> startWorker() {
        try {
            String msg = workerManager.startWorker();
            return ResponseEntity.ok(msg);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao iniciar WorkerServer: " + e.getMessage());
        }
    }

    @PostMapping("/stop")
    public ResponseEntity<String> stopWorker() {
        String msg = workerManager.stopWorker();
        return ResponseEntity.ok(msg);
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendData(@RequestBody Carro carro) {
        try {
            // 1. Serializa o objeto para XML e salva no diretório do WorkerServer
            XmlUtil.salvarComoXml(carro, XML_PATH);
            System.out.println("XML gerado em: " + XML_PATH);

            // 2. Reinicia o WorkerServer para que ele leia o XML
            workerManager.stopWorker();
            // Pequena espera para garantir que o processo foi finalizado
            Thread.sleep(5000);
            workerManager.startWorker();
            // Opcional: Aguardar que o WorkerServer esteja pronto para receber requisições

            // Aguarda até que o WorkerServer esteja disponível
            if (!waitForWorkerServer("http://localhost:6665/process", 10000)) {
                throw new RuntimeException("WorkerServer não respondeu dentro do tempo esperado.");
            }

            // 3. Chama o endpoint do WorkerServer para confirmar o processamento
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "http://localhost:6665/process", carro, String.class);
            return ResponseEntity.ok("WorkerServer respondeu: " + response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erro: " + e.getMessage());
        }
    }
}
