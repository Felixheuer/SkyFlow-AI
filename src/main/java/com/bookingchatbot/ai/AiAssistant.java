package com.bookingchatbot.ai;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import reactor.core.publisher.Flux;

/**
 * AI-powered assistant for customer support.
 * Uses LangChain4j for integration with OpenAI.
 */
@AiService
public interface AiAssistant {

    @SystemMessage("""
            You are a customer chat support agent for an airline named "Funnair".
            Respond in a friendly, helpful, and professional manner.
            You are interacting with customers through an online chat system.
            
            Before providing information about a booking or cancelling a booking,
            you MUST ensure you have the following information from the user:
            - Booking number
            - Customer first name
            - Customer last name
            
            Check the message history for this information before asking the user.
            Before changing a booking, you MUST ensure it is permitted by the terms.
            If there is a charge for the change, you MUST ask the user to consent before proceeding.
            
            Use the provided functions to fetch booking details, change bookings, and cancel bookings.
            Today is {{current_date}}.
            """)
    Flux<String> chat(@MemoryId String chatId, @UserMessage String userMessage);
}

