#!/bin/bash
set -e

echo "Starting Ollama service..."
ollama serve &
OLLAMA_PID=$!

# Wait for Ollama to be ready
echo "Waiting for Ollama to be ready..."
while ! curl -s http://localhost:11434/api/tags > /dev/null 2>&1; do
    sleep 2
done

echo "Ollama is ready!"

# Pull lightweight models if they don't exist
echo "Checking for required models..."

# 8GB 메모리용 경량 모델들
MODELS=("phi3:mini" "nomic-embed-text")

for model in "${MODELS[@]}"; do
    if ! ollama list | grep -q "$model"; then
        echo "Pulling model: $model"
        ollama pull "$model" || echo "Warning: Failed to pull $model"
    else
        echo "Model $model already exists"
    fi
done

echo "Starting FastAPI application..."
exec python -m uvicorn src.main:app --host 0.0.0.0 --port 8000 --workers 1