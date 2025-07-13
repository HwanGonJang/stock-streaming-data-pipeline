# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a real-time stock data streaming pipeline system built on Kubernetes that collects stock market data from Finnhub WebSocket API, processes it through Kafka and Spark Streaming, and stores it in Cassandra for visualization with Grafana.

## Architecture Components

### 1. Finnhub Producer (`./finnhub-producer/`)
- Python 3.9.6 application that streams real-time stock data from Finnhub WebSocket API
- Encodes messages using Avro schema and publishes to Kafka
- Includes throttling mechanism (1 message per second) to handle rate limits
- Test version available in `finnhub_producer_test.py` for debugging

### 2. News Producer (`./news-producer/`)
- Python application that streams news data from Finnhub API
- Stores news data directly in Cassandra without Kafka processing
- Monitors news for specific stock symbols

### 3. Kafka Message Broker (`./kafka/`)
- Kafka v6.2.0 with Zookeeper v6.2.0
- Handles message queuing between producers and stream processors
- Includes Kafdrop for monitoring

### 4. Stream Processors
- **Main Processor (`./stream-processor/`)**: Scala-based Spark Streaming application (Spark v3.5.3, Scala v2.12.18)
- **Light Processor (`./stream-processor-light/`)**: Python-based alternative with Redis caching for daily aggregates
- Both consume from Kafka, process trade data, and store in Cassandra

### 5. Cassandra Database (`./cassandra/`)
- Cassandra v3.11.17 for data storage
- Schema includes: `trades`, `daily_aggregates`, `news` tables
- Multi-node cluster setup

### 6. Grafana Visualization (`./grafana/`)
- Grafana v11.3.0 with Cassandra plugin
- Custom dashboards for real-time monitoring

### 7. Helm Chart (`./stock-streaming-data-pipeline-helm/`)
- Kubernetes deployment configuration
- Manages all components as a unified system

## Development Commands

### Python Components
```bash
# Install dependencies for any Python component
pip install -r requirements.txt

# Run individual components
python src/finnhub_producer.py
python src/news_producer.py  
python src/stream_processor.py
```

### Scala Stream Processor
```bash
# Build JAR (from stream-processor directory)
sbt assembly

# The output will be: target/scala-2.12/streamprocessor-assembly-1.0.jar
```

### Kubernetes Deployment
```bash
# Deploy via Helm
helm install stock-pipeline ./stock-streaming-data-pipeline-helm

# Check deployment status
kubectl get pods -n my-chart

# View logs
kubectl logs -f deployment/finnhub-producer -n my-chart
```

### Cassandra Operations
```bash
# Initialize schema
cqlsh -f cassandra/scripts/cassandra-setup.cql

# Connect to Cassandra
kubectl exec -it cassandra-0 -- cqlsh -u cassandra -p cassandra
```

## Key Configuration Files

- **Environment Variables**: Each component uses `.env` files for configuration
- **Avro Schemas**: `schemas/trades.avsc` defines the message format
- **Spark Configuration**: `stream-processor/src/main/resources/application.conf` and `deployment.conf`
- **Helm Values**: `stock-streaming-data-pipeline-helm/values.yaml`

## Data Flow

1. **Finnhub Producer** connects to WebSocket API and receives real-time trade data
2. **Kafka** queues the Avro-encoded messages in the `market` topic
3. **Stream Processor** consumes from Kafka, processes trades, and calculates running averages
4. **Cassandra** stores both raw trades and aggregated data
5. **Grafana** visualizes the data through custom dashboards

## Database Schema

### Trades Table
- Partition key: `symbol`
- Clustering key: `trade_timestamp` (DESC)
- Stores individual trade records

### Daily Aggregates Table  
- Partition key: `symbol`
- Clustering key: `trade_date` (DESC)
- Stores daily summary statistics

### News Table
- Partition key: `symbol`
- Clustering key: `datetime` (DESC)
- Stores news articles related to stocks

## Testing

- Test files available: `finnhub_producer_test.py`, `news_producer_test.py`
- Tests are designed to validate WebSocket connections and data processing without publishing to Kafka

## Common Issues

- **Rate Limiting**: Finnhub has API rate limits - the producer includes throttling
- **Avro Schema**: Ensure schema consistency across all components
- **Cassandra Connection**: Verify credentials and network connectivity
- **Kubernetes Resources**: Monitor resource usage, especially for Spark jobs