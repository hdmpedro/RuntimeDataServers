package com.workerserver.controller;

import com.workerserver.model.Carro;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/process")
public class ProcessController {

    @PostMapping
    public String processCarro(@RequestBody Carro carro) {
        System.out.println("Carro recebido via HTTP: " + carro);
        return "Carro processado com sucesso!";
    }




    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("WorkerServer está pronto!");
    }







}
