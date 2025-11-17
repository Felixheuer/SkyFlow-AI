const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';

export interface Booking {
  bookingNumber: string;
  firstName: string;
  lastName: string;
  date: string;
  bookingStatus: 'CONFIRMED' | 'CANCELLED';
  from: string;
  to: string;
  bookingClass: string;
}

export interface ChatMessage {
  chatId: string;
  message: string;
}

export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
}

class ApiClient {
  private baseUrl: string;

  constructor(baseUrl: string) {
    this.baseUrl = baseUrl;
  }

  private async handleResponse<T>(response: Response): Promise<T> {
    if (!response.ok) {
      let errorMessage = `HTTP ${response.status}: ${response.statusText}`;
      
      try {
        const errorData: ApiError = await response.json();
        errorMessage = errorData.message || errorMessage;
      } catch {
        // If parsing fails, use default error message
      }
      
      throw new Error(errorMessage);
    }
    
    return response.json();
  }

  async get<T>(endpoint: string): Promise<T> {
    try {
      const response = await fetch(`${this.baseUrl}${endpoint}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      });
      
      return this.handleResponse<T>(response);
    } catch (error) {
      console.error(`GET ${endpoint} failed:`, error);
      throw error;
    }
  }

  async post<T>(endpoint: string, data: unknown): Promise<T> {
    try {
      const response = await fetch(`${this.baseUrl}${endpoint}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
      });
      
      return this.handleResponse<T>(response);
    } catch (error) {
      console.error(`POST ${endpoint} failed:`, error);
      throw error;
    }
  }

  async postStream(endpoint: string, data: unknown): Promise<ReadableStream<Uint8Array> | null> {
    try {
      const response = await fetch(`${this.baseUrl}${endpoint}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
      });

      if (!response.ok) {
        let errorMessage = `HTTP ${response.status}: ${response.statusText}`;
        
        try {
          const errorData: ApiError = await response.json();
          errorMessage = errorData.message || errorMessage;
        } catch {
          // If parsing fails, use default error message
        }
        
        throw new Error(errorMessage);
      }

      return response.body;
    } catch (error) {
      console.error(`POST Stream ${endpoint} failed:`, error);
      throw error;
    }
  }

  async put<T>(endpoint: string, data: unknown): Promise<T> {
    try {
      const response = await fetch(`${this.baseUrl}${endpoint}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
      });
      
      return this.handleResponse<T>(response);
    } catch (error) {
      console.error(`PUT ${endpoint} failed:`, error);
      throw error;
    }
  }

  async delete<T>(endpoint: string): Promise<T> {
    try {
      const response = await fetch(`${this.baseUrl}${endpoint}`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
        },
      });
      
      return this.handleResponse<T>(response);
    } catch (error) {
      console.error(`DELETE ${endpoint} failed:`, error);
      throw error;
    }
  }
}

const apiClient = new ApiClient(API_BASE_URL);

/**
 * Fetch all bookings
 */
export async function getBookings(): Promise<Booking[]> {
  return apiClient.get<Booking[]>('/bookings');
}

/**
 * Get a specific booking
 */
export async function getBooking(
  bookingNumber: string,
  firstName: string,
  lastName: string
): Promise<Booking> {
  return apiClient.get<Booking>(
    `/bookings/${bookingNumber}?firstName=${encodeURIComponent(firstName)}&lastName=${encodeURIComponent(lastName)}`
  );
}

/**
 * Send a chat message to the AI assistant (streaming response)
 */
export async function sendChatMessage(
  chatId: string,
  message: string
): Promise<ReadableStream<Uint8Array> | null> {
  return apiClient.postStream('/chat', { chatId, message });
}

/**
 * Update a booking
 */
export async function updateBooking(
  bookingNumber: string,
  data: {
    firstName: string;
    lastName: string;
    newFlightDate: string;
    newDepartureAirport: string;
    newArrivalAirport: string;
  }
): Promise<Booking> {
  return apiClient.put<Booking>(`/bookings/${bookingNumber}`, data);
}

/**
 * Cancel a booking
 */
export async function cancelBooking(
  bookingNumber: string,
  firstName: string,
  lastName: string
): Promise<Booking> {
  return apiClient.delete<Booking>(
    `/bookings/${bookingNumber}?firstName=${encodeURIComponent(firstName)}&lastName=${encodeURIComponent(lastName)}`
  );
}

/**
 * Check API health
 */
export async function checkApiHealth(): Promise<boolean> {
  try {
    const response = await fetch(`${API_BASE_URL}/bookings`, {
      method: 'HEAD',
    });
    return response.ok;
  } catch {
    return false;
  }
}
