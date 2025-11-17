package com.bookingchatbot.controller;

import com.bookingchatbot.ai.AiAssistant;
import com.bookingchatbot.dto.ChatMessageDto;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * REST controller for AI chat operations.
 * Provides streaming chat responses.
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final AiAssistant aiAssistant;

    public ChatController(AiAssistant aiAssistant) {
        this.aiAssistant = aiAssistant;
    }

    @PostMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chat(@RequestBody ChatMessageDto request) {
        return aiAssistant.chat(request.chatId(), request.message());
    }
}

