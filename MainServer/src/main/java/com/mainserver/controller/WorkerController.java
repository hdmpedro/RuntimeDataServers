package com.mainserver.controller;

import com.mainserver.model.Carro;
import com.mainserver.service.WorkerManager;
import com.mainserver.util.XmlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/worker")
public class WorkerController {

    // private String hasId = "";
    private final WorkerManager workerManager;
    // private Integer novoPar = 0;
    private final RestTemplate restTemplate = new RestTemplate();
    private final String DATA_DIR = "c:\\PPROJETOS\\RuntimeDataServers-main\\WorkerServer\\target\\worker_data";
    // private final String XML_PATH = DATA_DIR + "/carro.xml";
    private String getUniqueXmlPath() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        return DATA_DIR + "/carro_" + timestamp + ".xml";
    }



    public boolean aguardarPeloWorkerServer(String url, int timeoutMillis) {
        long start = System.currentTimeMillis();

        String healthCheckUrl = "http://localhost:6669/process/health"; // URL fixa para simplificar

        while (System.currentTimeMillis() - start < timeoutMillis) {
            try {
                ResponseEntity<String> response = new RestTemplate().getForEntity(healthCheckUrl, String.class);
                if (response.getStatusCode().is2xxSuccessful()) {
                    return true;
                }
            } catch (Exception e) {
                System.err.println("Aguardando o workerserverr" + e.getMessage());
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
            String xmlPath = getUniqueXmlPath();
            XmlUtil.salvarComoXml(carro, xmlPath);
            System.out.println("XML gerado em: " + xmlPath);

            //workerManager.stopWorker();
            //Thread.sleep(10000);
             //workerManager.startWorker();

            if (!aguardarPeloWorkerServer("http://localhost:6669", 10000)) {
                throw new RuntimeException("WorkerServer não respondeu dentro do tempo esperado.");
            }

            ResponseEntity<String> response = restTemplate.postForEntity(
                    "http://localhost:6669/process", carro, String.class);
            return ResponseEntity.ok("WorkerServer respondeu: " + response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erro: " + e.getMessage());
        }

    }

}
