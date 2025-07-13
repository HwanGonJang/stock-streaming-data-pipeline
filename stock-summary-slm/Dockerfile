# Use Python 3.11 slim image
FROM python:3.11-slim

# Set environment variables
ENV PYTHONUNBUFFERED=1
ENV PYTHONDONTWRITEBYTECODE=1
ENV OLLAMA_HOST=0.0.0.0:11434

# Set work directory
WORKDIR /app

# Install system dependencies
RUN apt-get update && apt-get install -y \
    curl \
    build-essential \
    && rm -rf /var/lib/apt/lists/*

# Install Ollama
RUN curl -fsSL https://ollama.ai/install.sh | sh

# Copy requirements first for better caching
COPY requirements.txt .

# Install Python dependencies
RUN pip install --no-cache-dir -r requirements.txt

# Copy application code from src directory
COPY src/ ./src/

# Create directory for Ollama models (will be mounted as volume)
RUN mkdir -p /root/.ollama

# Use Python 3.11 slim image
FROM python:3.11-slim

# Set environment variables
ENV PYTHONUNBUFFERED=1
ENV PYTHONDONTWRITEBYTECODE=1
ENV OLLAMA_HOST=0.0.0.0:11434

# Set work directory
WORKDIR /app

# Install system dependencies
RUN apt-get update && apt-get install -y \
    curl \
    build-essential \
    bc \
    && rm -rf /var/lib/apt/lists/*

# Install Ollama
RUN curl -fsSL https://ollama.ai/install.sh | sh

# Copy requirements first for better caching
COPY requirements.txt .

# Install Python dependencies
RUN pip install --no-cache-dir -r requirements.txt

# Copy application code from src directory
COPY src/ ./src/

# COPY .env .

# Copy startup script
COPY start.sh .

# Create directory for Ollama models (will be mounted as volume)
RUN mkdir -p /root/.ollama

# Make startup script executable
RUN chmod +x /app/start.sh

# Expose ports
EXPOSE 8000 11434

# Health check
HEALTHCHECK --interval=30s --timeout=30s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8000/health || exit 1

# Start the application
CMD ["/app/start.sh"]

# # Dockerfile with GPU
# # NVIDIA CUDA 베이스 이미지 사용
# FROM nvidia/cuda:12.9.1-cudnn-devel-ubuntu24.04

# # GPU 관련 환경변수 추가
# ENV NVIDIA_VISIBLE_DEVICES=all
# ENV NVIDIA_DRIVER_CAPABILITIES=compute,utility
# ENV CUDA_VISIBLE_DEVICES=0

# # Python 3.11 설치
# RUN apt-get update && apt-get install -y \
#     python3.11 \
#     python3.11-dev \
#     python3-pip \
#     curl \
#     build-essential \
#     bc \
#     && rm -rf /var/lib/apt/lists/*

# # Python 심볼릭 링크
# RUN ln -s /usr/bin/python3.11 /usr/bin/python

# # 환경변수 설정
# ENV PYTHONUNBUFFERED=1
# ENV PYTHONDONTWRITEBYTECODE=1
# ENV OLLAMA_HOST=0.0.0.0:11434

# WORKDIR /app

# # Ollama 설치
# RUN curl -fsSL https://ollama.ai/install.sh | sh

# COPY requirements.txt .
# RUN pip install --no-cache-dir -r requirements.txt

# COPY src/ ./src/
# # COPY .env .
# COPY start.sh .

# RUN mkdir -p /root/.ollama
# RUN chmod +x /app/start.sh

# EXPOSE 8000 11434

# HEALTHCHECK --interval=30s --timeout=30s --start-period=60s --retries=3 \
#     CMD curl -f http://localhost:8000/health || exit 1

# CMD ["/app/start.sh"]