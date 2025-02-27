package com.mainserver.service;


import org.springframework.stereotype.Service;

import java.io.*;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Service
public class WorkerManager {
    private Process workerProcess;
    private final String WORKER_JAR = "c:/Users/pepef/Desktop/Projetos/RuntimeDataTransferServers/WorkerServer/target/WorkerServer-0.0.1-SNAPSHOT.jar";
    private final String DATA_DIR = "c:/Users/pepef/Desktop/Projetos/RuntimeDataTransferServers/WorkerServer/worker_data";

    public synchronized String startWorker() throws IOException {
        if (workerProcess != null && workerProcess.isAlive()) {
            return "WorkerServer já está rodando.";
        }
        // Cria o diretório se não existir
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // Inicia o WorkerServer passando o diretório como propriedade
        ProcessBuilder pb = new ProcessBuilder("java", "-Ddata.dir=" + DATA_DIR, "-jar", WORKER_JAR);
        pb.redirectErrorStream(true);
        workerProcess = pb.start();

        // Leitura assíncrona do output para debug
        StreamGobbler outputGobbler = new StreamGobbler(workerProcess.getInputStream(), System.out::println);
        Executors.newSingleThreadExecutor().submit(outputGobbler);

        return "WorkerServer iniciado.";
    }

    public synchronized String stopWorker() {
        if (workerProcess != null && workerProcess.isAlive()) {
            workerProcess.destroy();
            workerProcess = null;
            return "WorkerServer finalizado.";
        }
        return "Nenhum WorkerServer em execução.";
    }

    // Classe auxiliar para ler o output do processo
    private static class StreamGobbler implements Runnable {
        private InputStream inputStream;
        private Consumer<String> consumer;

        public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
            this.inputStream = inputStream;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            new BufferedReader(new InputStreamReader(inputStream)).lines()
                    .forEach(consumer);
        }
    }
}
