FROM python:3.9-slim

WORKDIR /app

# Copy requirements first to leverage Docker cache
COPY requirements.txt .
RUN pip install -r requirements.txt

# Copy source code and schemas
COPY src/ ./src/

# Set PYTHONPATH to include the src directory
ENV PYTHONPATH=/app

# Change working directory to src
WORKDIR /app/src

CMD ["python", "stream_processor.py"]