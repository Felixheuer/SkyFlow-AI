package com.bookingchatbot.dto;

/**
 * Data Transfer Object for chat messages.
 */
public record ChatMessageDto(
        String chatId,
        String message
) {
}

