'use client';

import { useState, useRef, useEffect } from 'react';
import { Card, CardContent, CardHeader } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { sendChatMessage } from '@/lib/api';
import { Loader2, Send, Bot, User, CheckCircle2, AlertCircle } from 'lucide-react';

export interface Message {
  role: 'user' | 'assistant';
  content: string;
  timestamp?: Date;
  status?: 'sending' | 'sent' | 'error';
}

interface ChatProps {
  chatId: string;
  onBookingUpdate?: () => void;
}

export function Chat({ chatId, onBookingUpdate }: ChatProps) {
  const [messages, setMessages] = useState<Message[]>([
    {
      role: 'assistant',
      content: 'Welcome to SkyFlow! I\'m your AI assistant. How can I help you with your booking today?',
      timestamp: new Date(),
      status: 'sent',
    },
  ]);
  const [input, setInput] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const scrollRef = useRef<HTMLDivElement>(null);
  const inputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    if (scrollRef.current) {
      scrollRef.current.scrollTop = scrollRef.current.scrollHeight;
    }
  }, [messages]);

  useEffect(() => {
    if (!isLoading && inputRef.current) {
      inputRef.current.focus();
    }
  }, [isLoading]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!input.trim() || isLoading) return;

    const userMessage = input.trim();
    setInput('');
    setIsLoading(true);
    setError(null);

    const userMsg: Message = {
      role: 'user',
      content: userMessage,
      timestamp: new Date(),
      status: 'sending',
    };
    setMessages((prev) => [...prev, userMsg]);

    try {
      const stream = await sendChatMessage(chatId, userMessage);
      if (!stream) throw new Error('No response stream');

      setMessages((prev) => {
        const newMessages = [...prev];
        newMessages[newMessages.length - 1].status = 'sent';
        return newMessages;
      });

      const reader = stream.getReader();
      const decoder = new TextDecoder();
      let assistantMessage = '';
      let isFirstChunk = true;

      while (true) {
        const { done, value } = await reader.read();
        if (done) break;

        const chunk = decoder.decode(value);
        const lines = chunk.split('\n');

        for (const line of lines) {
          if (line.startsWith('data:')) {
            // Don't trim to preserve spaces between words
            const data = line.slice(5);
            if (data && data.trim()) {
              assistantMessage += data;
              
              if (isFirstChunk) {
                const assistantMsg: Message = {
                  role: 'assistant',
                  content: data,
                  timestamp: new Date(),
                  status: 'sent',
                };
                setMessages((prev) => [...prev, assistantMsg]);
                isFirstChunk = false;
              } else {
                setMessages((prev) => {
                  const newMessages = [...prev];
                  newMessages[newMessages.length - 1].content = assistantMessage;
                  return newMessages;
                });
              }
            }
          }
        }
      }

      if (onBookingUpdate) {
        setTimeout(() => onBookingUpdate(), 500);
      }
    } catch (error) {
      console.error('Error sending message:', error);
      setError('Failed to send message. Please try again.');
      
      setMessages((prev) => {
        const newMessages = [...prev];
        newMessages[newMessages.length - 1].status = 'error';
        return newMessages;
      });
      
      setMessages((prev) => [
        ...prev,
        {
          role: 'assistant',
          content: 'I apologize, but I encountered an error. Please try again or contact support if the problem persists.',
          timestamp: new Date(),
          status: 'error',
        },
      ]);
    } finally {
      setIsLoading(false);
    }
  };

  const formatTime = (date: Date) => {
    return new Intl.DateTimeFormat('en-US', {
      hour: 'numeric',
      minute: 'numeric',
      hour12: true,
    }).format(date);
  };

  return (
    <Card className="flex flex-col h-full surface-panel overflow-hidden">
      <CardHeader className="border-b border-border/40 bg-gradient-to-r from-card/50 to-muted/20 backdrop-blur-xl py-4 px-4 md:px-6">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2 md:gap-3">
            <div className="relative flex-shrink-0">
              <div className="absolute inset-0 bg-primary/30 rounded-full animate-pulse-slow blur-md" />
              <div className="relative w-8 h-8 md:w-10 md:h-10 rounded-full gradient-primary shadow-glow flex items-center justify-center">
                <Bot className="h-4 w-4 md:h-5 md:w-5 text-primary-foreground" />
              </div>
            </div>
            <div className="min-w-0">
              <h3 className="text-base md:text-lg font-semibold text-foreground truncate">AI Support Assistant</h3>
              <p className="text-xs md:text-sm text-muted-foreground flex items-center gap-1.5">
                <span className="relative flex h-2 w-2 flex-shrink-0">
                  <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-success opacity-75"></span>
                  <span className="relative inline-flex rounded-full h-2 w-2 bg-success"></span>
                </span>
                <span className="truncate">Online • Ready to help</span>
              </p>
            </div>
          </div>
        </div>
      </CardHeader>
      
      <CardContent className="flex-1 flex flex-col p-0 bg-gradient-to-b from-background/50 to-muted/10 min-h-0 overflow-hidden">
        <div className="flex-1 overflow-y-auto p-3 md:p-4 lg:p-6 scrollbar-thin" ref={scrollRef}>
          <div className="space-y-3 md:space-y-4">
            {messages.map((message, index) => (
              <div
                key={index}
                className={`flex gap-2 md:gap-3 animate-slide-in ${
                  message.role === 'user' ? 'justify-end' : 'justify-start'
                }`}
              >
                {message.role === 'assistant' && (
                  <div className="flex-shrink-0 w-7 h-7 md:w-8 md:h-8 rounded-full glass border border-primary/30 flex items-center justify-center">
                    <Bot className="h-3.5 w-3.5 md:h-4 md:w-4 text-primary" />
                  </div>
                )}
                
                <div className={`flex flex-col gap-1 max-w-[90%] sm:max-w-[85%] md:max-w-[80%] lg:max-w-[75%]`}>
                  <div
                    className={`rounded-2xl px-3 py-2 md:px-4 md:py-3 shadow-professional transition-all duration-300 ease-in-out ${
                      message.role === 'user'
                        ? 'gradient-primary text-primary-foreground rounded-tr-sm shadow-glow'
                        : 'glass-strong border border-border/50 text-foreground rounded-tl-sm'
                    }`}
                  >
                    <p className="text-xs md:text-sm leading-relaxed break-words overflow-wrap-anywhere whitespace-normal">
                      {message.content}
                    </p>
                  </div>
                  
                  <div className={`flex items-center gap-2 px-2 ${
                    message.role === 'user' ? 'justify-end' : 'justify-start'
                  }`}>
                    <span className="text-xs text-muted-foreground">
                      {message.timestamp && formatTime(message.timestamp)}
                    </span>
                    {message.role === 'user' && message.status === 'sent' && (
                      <CheckCircle2 className="h-3 w-3 text-success" />
                    )}
                    {message.role === 'user' && message.status === 'error' && (
                      <AlertCircle className="h-3 w-3 text-destructive" />
                    )}
                  </div>
                </div>
                
                {message.role === 'user' && (
                  <div className="flex-shrink-0 w-7 h-7 md:w-8 md:h-8 rounded-full glass border border-border flex items-center justify-center">
                    <User className="h-3.5 w-3.5 md:h-4 md:w-4 text-foreground" />
                  </div>
                )}
              </div>
            ))}
            
            {isLoading && (
              <div className="flex gap-2 md:gap-3 justify-start animate-slide-in">
                <div className="flex-shrink-0 w-7 h-7 md:w-8 md:h-8 rounded-full glass border border-primary/30 flex items-center justify-center">
                  <Bot className="h-3.5 w-3.5 md:h-4 md:w-4 text-primary animate-pulse" />
                </div>
                <div className="glass-strong border border-border/50 rounded-2xl rounded-tl-sm px-3 py-2 md:px-4 md:py-3 shadow-professional">
                  <div className="flex items-center gap-2">
                    <div className="flex gap-1">
                      <span className="w-1.5 h-1.5 md:w-2 md:h-2 rounded-full bg-primary animate-bounce" style={{ animationDelay: '0ms' }} />
                      <span className="w-1.5 h-1.5 md:w-2 md:h-2 rounded-full bg-primary animate-bounce" style={{ animationDelay: '150ms' }} />
                      <span className="w-1.5 h-1.5 md:w-2 md:h-2 rounded-full bg-primary animate-bounce" style={{ animationDelay: '300ms' }} />
                    </div>
                    <span className="text-xs text-muted-foreground">AI is typing...</span>
                  </div>
                </div>
              </div>
            )}
          </div>
        </div>
        
        <div className="border-t border-border/40 p-3 md:p-4 lg:p-6 glass backdrop-blur-xl">
          {error && (
            <div className="mb-3 p-2.5 md:p-3 rounded-xl glass-strong border border-destructive/30 text-destructive text-xs md:text-sm flex items-center gap-2 animate-slide-in">
              <AlertCircle className="h-3.5 w-3.5 md:h-4 md:w-4 flex-shrink-0" />
              <span className="min-w-0 break-words">{error}</span>
            </div>
          )}
          
          <form onSubmit={handleSubmit} className="flex gap-2 md:gap-3">
            <Input
              ref={inputRef}
              value={input}
              onChange={(e) => setInput(e.target.value)}
              placeholder="Type your message..."
              disabled={isLoading}
              className="flex-1 focus-ring border-border/50 glass h-9 md:h-10 lg:h-11 text-sm"
              maxLength={500}
            />
            <Button 
              type="submit" 
              disabled={isLoading || !input.trim()}
              className="px-3 md:px-4 h-9 md:h-10 lg:h-11 gradient-primary shadow-glow hover:shadow-glow hover:scale-105 transition-all duration-300 ease-in-out border-0 flex-shrink-0"
            >
              {isLoading ? (
                <Loader2 className="h-3.5 w-3.5 md:h-4 md:w-4 animate-spin" />
              ) : (
                <Send className="h-3.5 w-3.5 md:h-4 md:w-4" />
              )}
              <span className="sr-only">Send message</span>
            </Button>
          </form>
          
          <p className="text-[10px] md:text-xs text-muted-foreground mt-1.5 md:mt-2 text-center">
            {input.length}/500 characters
          </p>
        </div>
      </CardContent>
    </Card>
  );
}
