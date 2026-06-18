package com.medikids.medikids.expose.web;

import com.medikids.medikids.expose.model.request.ChatbotRequest;
import com.medikids.medikids.process.service.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/chatbot")
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;

    @PostMapping("/message")
    public ResponseEntity<Map<String, String>> sendMessage(@RequestBody ChatbotRequest request) {
        try {
            String reply = chatbotService.chat(request);
            return ResponseEntity.ok(Map.of("reply", reply));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Map.of("reply", "Lo siento, estoy teniendo dificultades técnicas. Por favor intenta de nuevo o contáctanos directamente al 970 854 221. 📞")
            );
        }
    }
}
