.PHONY: help install start start-backend start-frontend build clean test dev

help:
	@echo "Booking Chatbot - Makefile Commands"
	@echo "===================================="
	@echo "make install         - Install all dependencies (Backend + Frontend)"
	@echo "make start           - Start both backend and frontend (use 2 terminals)"
	@echo "make dev             - Start backend and frontend in parallel"
	@echo "make start-backend   - Start only the backend server"
	@echo "make start-frontend  - Start only the frontend dev server"
	@echo "make build           - Build both backend and frontend for production"
	@echo "make clean           - Clean all build artifacts"
	@echo "make test            - Run backend tests"

install:
	@echo "Installing backend dependencies..."
	./mvnw clean install -DskipTests
	@echo "Installing frontend dependencies..."
	cd frontend && npm install
	@echo "✓ All dependencies installed successfully"

start:
	@echo "Start backend with: make start-backend"
	@echo "Start frontend with: make start-frontend (in another terminal)"

dev:
	@echo "Starting backend and frontend in parallel..."
	@echo "Backend: http://localhost:8080"
	@echo "Frontend: http://localhost:3000"
	@make -j 2 start-backend start-frontend

start-backend:
	@echo "Starting backend on http://localhost:8080..."
	@echo "Make sure OPENAI_API_KEY is set in your environment"
	./mvnw spring-boot:run

start-frontend:
	@echo "Starting frontend on http://localhost:3000..."
	cd frontend && npm run dev

build:
	@echo "Building backend..."
	./mvnw clean package -DskipTests
	@echo "Building frontend..."
	cd frontend && npm run build
	@echo "✓ Build completed successfully"

clean:
	@echo "Cleaning build artifacts..."
	./mvnw clean
	rm -rf frontend/.next
	rm -rf frontend/node_modules
	rm -rf frontend/out
	@echo "✓ Cleanup completed"

test:
	@echo "Running backend tests..."
	./mvnw test
